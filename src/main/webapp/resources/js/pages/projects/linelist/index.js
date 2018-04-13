import React from "react";
import { render } from "react-dom";
import configureStore from "./redux/configureStore";

// Linelist Table
import Root from "./components/Root";
import { fetchMetadataFields, fetchMetadataEntries } from "./apis";
import { metadataLoadingSaga } from "./redux/modules/metadata";

// Get the project id from the window object:
const PROJECT_ID = window.project.id;

const store = configureStore();

// Initialize the linelist table.
store.runSaga(
  metadataLoadingSaga,
  fetchMetadataFields,
  fetchMetadataEntries,
  PROJECT_ID
);

render(<Root store={store} />, document.querySelector("#root"));
