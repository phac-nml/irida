import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Provider } from "react-redux";
import { createRoot } from "react-dom/client";
import { SampleMetadataImportComplete } from "./components/SampleMetadataImportComplete";
import { SampleMetadataImportMapHeaders } from "./components/SampleMetadataImportMapHeaders";
import { SampleMetadataImportReview } from "./components/SampleMetadataImportReview";
import { SampleMetadataImportUploadFile } from "./components/SampleMetadataImportUploadFile";
import { setBaseUrl } from "../../../utilities/url-utilities";
import store from "./store";

/*
Router for sample metadata importer.
For more information on the browser router see: https://reactrouter.com/web/api/BrowserRouter
 */

const ROOT_ELEMENT = document.querySelector("#samples-metadata-import-root");
const root = createRoot(ROOT_ELEMENT);
root.render(
  <Provider store={store}>
    <BrowserRouter basename={setBaseUrl("/projects")}>
      <Routes>
        <Route
          path="/:projectId/sample-metadata/upload/file"
          element={<SampleMetadataImportUploadFile />}
        />
        <Route
          path="/:projectId/sample-metadata/upload/headers"
          element={<SampleMetadataImportMapHeaders />}
        />
        <Route
          path="/:projectId/sample-metadata/upload/review"
          element={<SampleMetadataImportReview />}
        />
        <Route
          path="/:projectId/sample-metadata/upload/complete"
          element={<SampleMetadataImportComplete />}
        />
      </Routes>
    </BrowserRouter>
  </Provider>
);
