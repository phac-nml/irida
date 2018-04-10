import React from "react";
import { Provider } from "react-redux";
import { render } from "react-dom";
import createStore from "./redux/create";

// Linelist Table
import { fetchMetadataFields, fetchMetadataEntries } from "./apis";
import { metadataLoadingSaga } from "./redux/modules/metadata";

// UI
import App from "./containers/App";

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

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.querySelector("#root")
);
