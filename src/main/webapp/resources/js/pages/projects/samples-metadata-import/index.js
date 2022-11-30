import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Provider } from "react-redux";
import { render } from "react-dom";
import { SampleMetadataImportComplete } from "./components/SampleMetadataImportComplete";
import { SampleMetadataImportMapColumns } from "./components/SampleMetadataImportMapColumns";
import { SampleMetadataImportReview } from "./components/SampleMetadataImportReview";
import { SampleMetadataImportSelectFile } from "./components/SampleMetadataImportSelectFile";
import { setBaseUrl } from "../../../utilities/url-utilities";
import store from "./redux/store";

/*
Router for sample metadata importer.
For more information on the browser router see: https://reactrouter.com/web/api/BrowserRouter
 */

render(
  <Provider store={store}>
    <BrowserRouter basename={setBaseUrl("/projects")}>
      <Routes>
        <Route
          path="/:projectId/sample-metadata/upload/file"
          element={<SampleMetadataImportSelectFile />}
        />
        <Route
          path="/:projectId/sample-metadata/upload/columns"
          element={<SampleMetadataImportMapColumns />}
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
  </Provider>,
  document.querySelector("#samples-metadata-import-root")
);
