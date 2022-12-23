import {
  CreateSampleItem,
  MetadataItem,
  SamplesResponse,
  UpdateSampleItem,
} from "../../../../apis/projects/samples";
import {
  MetadataHeaderItem,
  MetadataSaveDetailsItem,
  updatePercentComplete,
} from "./importReducer";
import { ImportDispatch } from "./store";
import { chunkArray } from "../../../../utilities/array-utilities";

export function createPromiseList(
  sampleList: CreateSampleItem[] | UpdateSampleItem[],
  sampleFunction: (p: {
    body: any[];
    projectId: string;
  }) => Promise<SamplesResponse>,
  projectId: string,
  totalCount: number,
  metadataSaveDetails: Record<string, MetadataSaveDetailsItem>,
  dispatch: ImportDispatch
) {
  const newMetadataSaveDetails = { ...metadataSaveDetails };
  const promiseList: Promise<void>[] = [];
  const chunkedSampleList = chunkArray(sampleList);

  chunkedSampleList.forEach((chunk) => {
    promiseList.push(
      sampleFunction({ projectId, body: chunk }).then(({ responses }) => {
        dispatch(
          updatePercentComplete(
            Math.round((Object.keys(responses).length / totalCount) * 100)
          )
        );
        Object.keys(responses).forEach((key) => {
          const { error, errorMessage } = responses[key];
          newMetadataSaveDetails[key] = {
            saved: !error,
            error: errorMessage,
          };
        });
      })
    );
  });

  return { promiseList, newMetadataSaveDetails };
}

/**
 * Create a list of metadata fields and their values for each sample.
 * @param sampleNameColumn - the name of the header that represents the sample name column
 * @param headers - a list of the table headers
 * @param metadataItem - the data of a row in the table representing the metadata of a sample
 */
export function createMetadataFields(
  sampleNameColumn: string,
  headers: MetadataHeaderItem[],
  metadataItem: MetadataItem
) {
  return Object.entries(metadataItem)
    .filter(
      ([key]) =>
        headers.map((header) => header.name).includes(key) &&
        key !== sampleNameColumn
    )
    .map(([key, value]) => ({
      field: key,
      value,
    }));
}
