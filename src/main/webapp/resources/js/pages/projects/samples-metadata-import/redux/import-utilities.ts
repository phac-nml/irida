import {
  MetadataItem,
  SampleItemResponse,
} from "../../../../apis/projects/samples";
import {
  MetadataHeaderItem,
  MetadataSaveDetailsItem,
  setMetadataSaveDetails,
} from "./importReducer";
import { ImportDispatch } from "./store";

/**
 * Save the responses from creating and updating samples in a hash.
 * @param responses - from creating and updating samples
 * @param metadataSaveDetails - the hash storing the responses
 * @param dispatch - a function of the Redux store to dispatch an action to trigger a state change
 */
export function updateMetadataSaveDetails(
  responses: Record<string, SampleItemResponse>,
  metadataSaveDetails: Record<string, MetadataSaveDetailsItem>,
  dispatch: ImportDispatch
) {
  Object.keys(responses).forEach((key) => {
    const { error, errorMessage } = responses[key];
    metadataSaveDetails[key] = {
      saved: !error,
      error: errorMessage,
    };
  });
  dispatch(setMetadataSaveDetails(Object.assign({}, metadataSaveDetails)));
  return metadataSaveDetails;
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
