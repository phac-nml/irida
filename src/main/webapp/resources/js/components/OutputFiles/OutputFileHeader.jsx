/**
 * Component to render a file header (filename and download button)
 * for each file on preview page
 */

import React from "react";
import { Button, Space, Typography } from "antd";
import { convertFileSize } from "../../utilities/file-utilities";
import { downloadIndividualOutputFile } from "../../apis/analyses/analyses";
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
 * @param {array} extras - Extra buttons to be rendered
 *
 * @returns {Element} - Returns a component which has a file header with download button
 */

export function OutputFileHeader({ output, extras = [] }) {
  return (
    <OutputFileHeaderWrapper>
      <div>
        <Text
          style={{
            fontSize: FONT_SIZE_DEFAULT,
          }}
          className="t-file-name"
        >
          {`${output.toolName} ${output.toolVersion} - ${output.outputName} - ${output.filename}`}
        </Text>
      </div>
      <Space>
        {extras}
        <Button
          style={{
            marginLeft: SPACE_XS,
          }}
          onClick={() =>
            downloadIndividualOutputFile(output.analysisSubmissionId, output.id)
          }
          icon={<IconDownloadFile />}
          className="t-download-output-file-btn"
        >
          {`${output.filename} (${convertFileSize(output.fileSizeBytes)})`}
        </Button>
      </Space>
    </OutputFileHeaderWrapper>
  );
}
