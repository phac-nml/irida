/**
 * Root file for the linelist page.
 */
import MetadataEntryApi from "./api/metadataEntryApi";
import MetadataFieldApi from "./api/metadataFieldApi";

/**
 * These are only here now to test the API.
 */
MetadataEntryApi.getAllMetadataEntries(window.project.id)
  .then(result => console.log(result))
  .catch(err => console.error(err));

MetadataFieldApi.getAllMetadataFields(window.project.id)
  .then(result => console.log(result))
  .catch(err => console.error(err));
