/**
 * @File component renders a preview of html output files.
 */

import React from "react";
import { Divider } from "antd";
import DOMPurify from 'dompurify';
import { OutputFileHeader } from "../../../components/OutputFiles";
import { getHtmlFile } from "../../../apis/analysis/analysis";
import styled from "styled-components";
import { SPACE_XS } from "../../../styles/spacing";
import { grey4 } from "../../../styles/colors";

const HtmlOutputWrapper = styled.div`
  max-height: 500px;
  width: 100%;
  margin-bottom: ${SPACE_XS};
  border: solid 1px ${grey4};
  overflow: auto;
  padding: ${SPACE_XS};
`;

export default function AnalysisHtmlPreview({ output }) {
  const [htmlOutput, setHtmlOutput] = React.useState(null);

  React.useEffect(() => {
    getHtmlFile(output.analysisSubmissionId, output.filename).then(html => {
      // Sanitize the html output before setting it in the state.
      setHtmlOutput(DOMPurify.sanitize(html));
    });
  }, []);

  /*
   * Displays the html output as well
   * as the name of the file and a download button for
   * the file.
   */
  function displayHtmlOutput() {
    return (
      <div>
        <OutputFileHeader output={output} />
        <Divider />
        <HtmlOutputWrapper
          dangerouslySetInnerHTML={{
            __html: htmlOutput
          }}
        />
      </div>
    );
  }
  return <>{displayHtmlOutput()}</>;
}
