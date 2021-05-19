import React from "react";
import { render } from "react-dom";
import { SampleMetadataFileUploader } from "./components/SampleMetadataFileUploader";

render(<SampleMetadataFileUploader />, document.querySelector("#samples-metadata-import-root"));