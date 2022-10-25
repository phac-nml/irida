import uniqolor from "uniqolor";
import {
  getMetadata,
  getMetadataTemplateFields,
  getMetadataTemplates,
  getNewickTree,
  Metadata,
  MetadataItem,
  MetadataResponse,
  MetadataTemplatesResponse,
  NewickTreeResponse,
} from "../../../apis/analysis/analysis";
import {
  MetadataColourMap,
  Template,
  TreeProperties,
} from "../../../types/phylocanvas";
import { LoadingState } from "./treeSlice";

const EMPTY_COLOUR = "#ffffff";

/**
 * Generate a colour for each unique value per metadata term
 * @param metadata Map of sampleNames to Map of metadata terms to metadata values
 * @param terms List of metadata terms
 * @return Map of metadata terms and values to colours for phylocanvas to consume
 */
export function generateColourMap({
  metadata = {},
  terms = [],
}: Omit<MetadataResponse, "error">): MetadataColourMap {
  const colourMap: MetadataColourMap = terms.reduce(
    (prev, curr) => ({ ...prev, [curr]: {} }),
    {}
  );

  Object.values(metadata).forEach((sampleMetadata) => {
    terms.forEach((term) => {
      const value = term in sampleMetadata ? sampleMetadata[term].value : "";
      if (!(value in colourMap[term])) {
        if (value === "") {
          colourMap[term][value] = EMPTY_COLOUR;
        } else {
          colourMap[term][value] = uniqolor(value)["color"];
        }
      }
    });
  });

  return colourMap;
}

/**
 * Format the metadata into an object that can be consumed by Phylocanvas.
 *  { leaf-label: { templateMetadataField : { label, color} }}
 * @param metadata Map of sampleNames to Map of metadata terms to metadata values
 * @param terms list of metadata terms
 * @param colourMap Map of metadata terms and values to colours for phylocanvas to consume
 * @return  Map of metadata with colours for Phylocanvas to consume
 */
export function formatMetadata(
  metadata: Metadata,
  terms: string[],
  colourMap: MetadataColourMap
): Metadata {
  const sampleMetadataTemplate = () => {
    return terms.reduce((prev, curr) => {
      return Object.assign(prev, {
        [curr]: { label: "", colour: colourMap[curr][""] },
      });
    }, {});
  };

  const formatSampleMetadata = (sampleMetadata: MetadataItem) => {
    return Object.keys(sampleMetadata).reduce((prev, curr) => {
      return Object.assign(prev, {
        [curr]: {
          label: sampleMetadata[curr].value,
          colour: colourMap[curr][sampleMetadata[curr].value],
        },
      });
    }, {});
  };

  return Object.keys(metadata).reduce((prev, curr) => {
    return Object.assign(prev, {
      [curr]: {
        ...sampleMetadataTemplate(),
        ...formatSampleMetadata(metadata[curr]),
      },
    });
  }, {});
}

export type FetchTreeAndMetadataResponse = {
  loadingState: LoadingState;
  analysisId: number;
  treeProps: Partial<TreeProperties>;
  terms: string[];
  metadata: Metadata;
  metadataColourMap: MetadataColourMap;
  templates: { id: number; label: string; fields?: string[] }[];
};

export async function fetchTreeAndMetadata(
  analysisId: number
): Promise<FetchTreeAndMetadataResponse> {
  const promises: [
    NewickTreeResponse,
    MetadataResponse,
    MetadataTemplatesResponse
  ] = [
    await getNewickTree(analysisId),
    await getMetadata(analysisId),
    await getMetadataTemplates(analysisId),
  ];

  const [newickData, { metadata = {}, terms = [] }, metadataTemplateData] =
    await Promise.all(promises);

  // Check for errors
  if (!newickData.newick) {
    throw new Error(newickData.message ? newickData.message : newickData.error);
  }

  const metadataColourMap = generateColourMap({ metadata, terms });

  const formattedMetadata = formatMetadata(metadata, terms, metadataColourMap);

  return {
    loadingState:
      newickData.newick.length === 0
        ? LoadingState.empty
        : LoadingState.complete,
    analysisId,
    treeProps: {
      source: newickData.newick,
      showBlockHeaders: true,
      metadata: formattedMetadata,
      blocks: terms,
    },
    terms: terms,
    metadata: metadata,
    metadataColourMap: metadataColourMap,
    templates: metadataTemplateData.templates,
  };
}

type FetchMetadataTemplateFieldsParams = {
  index: number;
  analysisId: number;
  terms: string[];
  templates: Template[];
};

export type FetchMetadataTemplateFieldsResponse = {
  fields: string[];
  index?: number;
};

/**
 * Fetch template metadata fields for a specific template
 *   1. If the template is "All Fields" --> return all fields
 *   2. If the template doesn't exist --> return no fields
 *   3. If the template fields have already been downloaded --> return them
 *   4. If not downloaded --> fetch them from server --> return them
 * @param param0
 * @returns
 */
export async function fetchMetadataTemplateFields({
  index,
  analysisId,
  terms,
  templates,
}: FetchMetadataTemplateFieldsParams): Promise<FetchMetadataTemplateFieldsResponse> {
  if (index === -1) {
    return { fields: terms };
  } else if (index > templates.length) {
    return { fields: [] };
  } else {
    if (templates[index]?.fields !== undefined) {
      const { fields } = templates[index];
      return {
        fields: fields === undefined ? [] : fields,
      };
    } else {
      const data = await getMetadataTemplateFields(
        analysisId,
        templates[index].id
      );
      const fields =
        data.fields?.filter((field) => terms.includes(field)) || [];
      return {
        fields,
        index,
      };
    }
  }
}
