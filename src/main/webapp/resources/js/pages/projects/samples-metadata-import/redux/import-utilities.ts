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

export function updateMetadataSaveDetails(
  responses: Record<string, SampleItemResponse>,
  metadataSaveDetails: Record<string, MetadataSaveDetailsItem>,
  dispatch: ImportDispatch
) {
  Object.keys(responses).map((key) => {
    const { error, errorMessage } = responses[key];
    metadataSaveDetails[key] = {
      saved: !error,
      error: errorMessage,
    };
  });
  dispatch(setMetadataSaveDetails(Object.assign({}, metadataSaveDetails)));
  return metadataSaveDetails;
}

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
