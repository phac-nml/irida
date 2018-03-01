/**
 * Root file for the linelist page.
 */
import MetadataEntriesApi from "./api/metadataEntryApi";
import MetadataFieldsApi from "./api/metadataFieldsApi";

/**
 * These are only here now to test the API.
 */
MetadataEntriesApi.getAllMetadataEntries(window.project.id)
  .then(result => console.log(result))
  .catch(err => console.error(err));

MetadataFieldsApi.getAllMetadataFields(window.project.id)
  .then(result => console.log(result))
  .catch(err => console.error(err));
