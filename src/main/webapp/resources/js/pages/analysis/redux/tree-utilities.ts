import {getMetadata, getMetadataTemplates, getNewickTree} from "../../../apis/analysis/analysis";
import {formatMetadata, generateColourMap} from "../tree-utilities";
import {LoadingState, TreeState} from "./treeSlice";
import uniqolor from "uniqolor";

const EMPTY_COLOUR = "#ffffff";

type GenerateColourMapReturn = Record<string, string>;

/**
 * Generate a colour for each unique value per metadata term
 * @param {object} metadata Map of sampleNames to Map of metadata terms to metadata values
 * @param {array} terms List of metadata terms
 * @return {object} Map of metadata terms and values to colours for phylocanvas to consume
 */
export function generateColourMap(metadata: Record<string, Record<string, string>>, terms : string[]): GenerateColourMapReturn {
    const colourMap : GenerateColourMapReturn = terms.reduce((prev, curr) => ({...prev, [curr]: {}}), {});

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

export type FetchTreeAndMetadataReturn = Promise<Partial<TreeState> | string>;

export async function fetchTreeAndMetadata(analysisId: number, {rejectWithValue}: { rejectWithValue: (error: string) => string }): FetchTreeAndMetadataReturn {
    const promises = [
        await getNewickTree(analysisId),
        await getMetadata(analysisId),
        await getMetadataTemplates(analysisId),
    ];

    const [newickData, metadataData, metadataTemplateData] = await Promise.all(
        promises
    );

    // Check for errors
    if (!newickData.newick) {
        return rejectWithValue(
            newickData.message ? newickData.message : newickData.error.message
        );
    }

    const metadataColourMap = generateColourMap(
        metadataData.metadata,
        metadataData.terms
    );

    const formattedMetadata = formatMetadata(
        metadataData.metadata,
        metadataData.terms,
        metadataColourMap
    );

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
}
