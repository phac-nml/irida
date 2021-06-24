import React from "react";
import { Router } from "@reach/router";
import { render } from "react-dom";
import { SampleMetadataImportFileUploader } from "./components/SampleMetadataImportFileUploader";
import { SampleMetadataImportHeaders } from "./components/SampleMetadataImportHeaders";
import { setBaseUrl } from "../../../utilities/url-utilities";

render(
    <Router>
        <SampleMetadataImportFileUploader path={setBaseUrl("/projects/:projectId/sample-metadata/upload2")} />
        <SampleMetadataImportHeaders path={setBaseUrl("/upload/getMetadata")} />
    </Router>,
    document.querySelector("#samples-metadata-import-root")
);
