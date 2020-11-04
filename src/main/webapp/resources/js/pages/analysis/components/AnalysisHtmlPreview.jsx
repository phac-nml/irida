/**
 * @File component renders a preview of html output files.
 */

import React from "react";
import { Button, Divider } from "antd";
import Iframe from "react-iframe";
import { OutputFileHeader } from "../../../components/OutputFiles";
import { setBaseUrl } from "../../../utilities/url-utilities";

export default function AnalysisHtmlPreview({ output }) {
  /*
   * Displays the html output as well
   * as the name of the file and a download button for
   * the file.
   */
  function displayHtmlOutput() {
    const URL = setBaseUrl(
      `/analysis/${output.analysisSubmissionId}/html-output?filename=${output.filename}`
    );

    const openPopup = () => window.open(URL);

    return (
      <div>
        <OutputFileHeader
          output={output}
          extras={[
            <Button onClick={openPopup} key="open-html">
              Open in new window
            </Button>,
          ]}
        />
        <Divider />
        <Iframe
          url={URL}
          width="100%"
          height="600"
          style={{ border: "none", minHeight: 600 }}
          frameBorder={0}
          id="html-output-iframe"
          display="initial"
          position="relative"
        />
      </div>
    );
  }
  return <>{displayHtmlOutput()}</>;
}
