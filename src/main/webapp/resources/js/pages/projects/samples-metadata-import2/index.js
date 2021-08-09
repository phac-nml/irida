import React from "react";
import { Router } from "@reach/router";
import { Provider } from "react-redux";
import { render } from "react-dom";
import { SampleMetadataImportUploadFile } from "./components/SampleMetadataImportUploadFile";
import { SampleMetadataImportMapHeaders } from "./components/SampleMetadataImportMapHeaders";
import { SampleMetadataImportComplete } from "./components/SampleMetadataImportComplete";
import { setBaseUrl } from "../../../utilities/url-utilities";
import store from "./store";

render(
    <Provider store={store}>
        <Router basepath={setBaseUrl("/projects/:projectId/sample-metadata/upload2")}>
            <SampleMetadataImportUploadFile path="/file" />
            <SampleMetadataImportMapHeaders path="/headers" />
            <SampleMetadataImportComplete path="/complete" />
        </Router>
    </Provider>,
    document.querySelector("#samples-metadata-import-root")
);