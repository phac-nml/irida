import React from "react";
import { Router } from "@reach/router";
import { Provider } from "react-redux";
import { render } from "react-dom";
import { SampleMetadataImportUploadFile } from "./components/SampleMetadataImportUploadFile";
import { SampleMetadataImportMapHeaders } from "./components/SampleMetadataImportMapHeaders";
import { setBaseUrl } from "../../../utilities/url-utilities";
import store from "./store";

render(
    <Provider store={store}>
        <Router basepath={setBaseUrl("/projects/sample-metadata/upload2")}>
            <SampleMetadataImportUploadFile path="/file" projectId={window.project.id} />
            <SampleMetadataImportMapHeaders path="/headers" projectId={window.project.id} />
        </Router>
    </Provider>,
    document.querySelector("#samples-metadata-import-root")
);