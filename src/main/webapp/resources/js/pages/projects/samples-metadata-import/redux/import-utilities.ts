import { MetadataItem } from "../../../../apis/projects/samples";
import { MetadataHeaderItem } from "./importReducer";

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
