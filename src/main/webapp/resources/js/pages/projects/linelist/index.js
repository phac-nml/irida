import createStore from "./redux/create";

// Linelist Table
import { fetchMetadataFields, fetchMetadataEntries } from "./apis";
import { metadataLoadingSaga } from "./redux/modules/metadata";

// Get the project id from the window object:
const PROJECT_ID = window.project.id;

const store = createStore();

// Initialize the linelist table.
store.runSaga(
  metadataLoadingSaga,
  fetchMetadataFields,
  fetchMetadataEntries,
  PROJECT_ID
);
