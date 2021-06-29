import React from "react";
import { Router } from "@reach/router";
import { Provider } from "react-redux";
import { render } from "react-dom";
import { SampleMetadataImportFileUploader } from "./components/SampleMetadataImportFileUploader";
import { SampleMetadataImportHeaders } from "./components/SampleMetadataImportHeaders";
import { setBaseUrl } from "../../../utilities/url-utilities";
import store from "./store";

render(
    <Provider store={store}>
        <Router basepath={setBaseUrl("/projects/:projectId/sample-metadata/upload2")}>
            <SampleMetadataImportFileUploader path="/file" />
            <SampleMetadataImportHeaders path="/headers" />
        </Router>
    </Provider>,
    document.querySelector("#samples-metadata-import-root")
);