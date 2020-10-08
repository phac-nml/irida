/**
 * Component to render a file header (filename and download button)
 * for each file on preview page
 */

import React from "react";
import PropTypes from "prop-types";
import { Button, Typography } from "antd";
import { convertFileSize } from "../../utilities/file-utilities";
import { downloadOutputFile } from "../../apis/analysis/analysis";
import { SPACE_MD, SPACE_XS } from "../../styles/spacing";
import { FONT_SIZE_DEFAULT } from "../../styles/fonts";
import styled from "styled-components";
import { IconDownloadFile } from "../icons/Icons";

const { Text } = Typography;

const OutputFileHeaderWrapper = styled.div`
  margin-bottom: ${SPACE_MD};
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

/**
 * Stateless UI component for creating the file header with download button
 * @param {object} output - The current output file
 *
 * @returns {Element} - Returns a component which has a file header with download button
 */

export function OutputFileHeader({ output }) {
  return (
    <OutputFileHeaderWrapper>
      <div>
        <Text
          style={{
            fontSize: FONT_SIZE_DEFAULT
          }}
          className="t-file-name"
        >
          {`${output.toolName} ${output.toolVersion} - ${output.outputName} - ${output.filename}`}
        </Text>
      </div>
      <div>
        <Button
          style={{
            marginLeft: SPACE_XS
          }}
          onClick={() =>
            downloadOutputFile({
              submissionId: output.analysisSubmissionId,
              fileId: output.id
            })
          }
          icon={<IconDownloadFile />}
          className="t-download-output-file-btn"
        >
          {`${output.filename} (${convertFileSize(output.fileSizeBytes)})`}
        </Button>
      </div>
    </OutputFileHeaderWrapper>
  );
}

OutputFileHeader.propTypes = {
  /*Output file object*/
  output: PropTypes.object.isRequired
};
