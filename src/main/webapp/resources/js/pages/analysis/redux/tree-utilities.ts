import {
  getMetadata,
  getMetadataTemplates,
  getNewickTree,
  Metadata,
  MetadataItem,
  MetadataResponse,
  NewickTreeResponse,
} from "../../../apis/analysis/analysis";
import { LoadingState, TreeState } from "./treeSlice";
import uniqolor from "uniqolor";
import { MetadataColourMap, TreeProperties } from "../../../types/phylocanvas";

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
): Pick<TreeProperties, "metadata"> {
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

export type FetchTreeAndMetadataReturn = Promise<Partial<TreeState> | string>;

export async function fetchTreeAndMetadata(
  analysisId: number,
  { rejectWithValue }: { rejectWithValue: (error: string) => string }
): FetchTreeAndMetadataReturn {
  const promises: [NewickTreeResponse, MetadataResponse, any] = [
    await getNewickTree(analysisId),
    await getMetadata(analysisId),
    await getMetadataTemplates(analysisId),
  ];

  const [newickData, { metadata = {}, terms = [] }, metadataTemplateData] =
    await Promise.all(promises);

  console.log({ metadata, terms });

  // Check for errors
  if (!newickData.newick) {
    return rejectWithValue(
      newickData.message ? newickData.message : newickData.error.message
    );
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
      blocks: metadataData.terms,
    },
    terms: metadataData.terms,
    metadata: metadataData.metadata,
    metadataColourMap: metadataColourMap,
    templates: metadataTemplateData.templates,
  };
}
