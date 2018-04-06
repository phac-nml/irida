/**
 * Root file for the linelist page.
 */
import { createStore } from "redux";
import tableReducer from "./containers/MetadataTable/reducer";

import MetadataEntryApi from "./api/metadataEntryApi";
import MetadataFieldApi from "./api/metadataFieldApi";

const store = createStore(tableReducer);

/**
 * These are only here now to test the API.
 */
MetadataEntryApi.getAllMetadataEntries(window.project.id).then(
  result => console.log(result),
  err => console.error(err)
);

MetadataFieldApi.getAllMetadataFields(window.project.id).then(
  result => console.log(result),
  err => console.error(err)
);
