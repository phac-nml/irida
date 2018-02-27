/**
 * Root file for the linelist page.
 */
import MetadataApi from "./api/metadataApi";

/**
 * These are only here now to test the API.
 */
MetadataApi.getAllMetadataEntries(window.project.id)
  .then(result => console.log(result))
  .catch(err => console.error(err));

MetadataApi.getAllMetadataFields(window.project.id)
  .then(result => console.log(result))
  .catch(err => console.error(err));
