import React from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import { Provider } from "react-redux";
import { render } from "react-dom";
import { SampleMetadataImportUploadFile } from "./components/SampleMetadataImportUploadFile";
import { SampleMetadataImportMapHeaders } from "./components/SampleMetadataImportMapHeaders";
import { SampleMetadataImportReview } from "./components/SampleMetadataImportReview";
import { SampleMetadataImportComplete } from "./components/SampleMetadataImportComplete";
import { setBaseUrl } from "../../../utilities/url-utilities";
import store from "./store";

render(
  <Provider store={store}>
    <BrowserRouter basename={setBaseUrl("/projects")}>
      <Switch>
        <Route path="/:projectId/sample-metadata/upload2/file">
          <SampleMetadataImportUploadFile />
        </Route>
        <Route path="/:projectId/sample-metadata/upload2/headers">
          <SampleMetadataImportMapHeaders />
        </Route>
        <Route path="/:projectId/sample-metadata/upload2/review">
          <SampleMetadataImportReview />
        </Route>
        <Route path="/:projectId/sample-metadata/upload2/complete">
          <SampleMetadataImportComplete />
        </Route>
      </Switch>
    </BrowserRouter>
  </Provider>,
  document.querySelector("#samples-metadata-import-root")
);