/**
 * Component to render a file header with download button
 * for each file on preview page
 */

import React from "react";
import PropTypes from "prop-types";
import { Button, Icon, Col, Typography } from "antd";
import { convertFileSize } from "../../utilities/file.utilities";
import { downloadOutputFile } from "../../apis/analysis/analysis";
import { SPACE_MD, SPACE_XS } from "../../styles/spacing";
import { FONT_SIZE_DEFAULT } from "../../styles/fonts";

const { Text } = Typography;

/**
 * Stateless UI component for creating the file header with download button
 * @param {object} output - The current output file
 *
 * @returns {Element} - Returns a component which has a file header with download button
 */

export function OutputFileHeader({ output }) {
  return (
    <div
      style={{
        marginBottom: SPACE_MD,
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center"
      }}
    >
      <Col>
        <Text
          style={{
            fontSize: FONT_SIZE_DEFAULT
          }}
        >
          {`${output.toolName} ${output.toolVersion} - ${output.outputName} - ${output.filename}`}
        </Text>
      </Col>
      <Col>
        <Button
          style={{
            marginLeft: SPACE_XS
          }}
          onClick={() =>
            downloadOutputFile(output.analysisSubmissionId, output.id)
          }
        >
          <Icon type="download" />
          {`${output.filename} (${convertFileSize(output.fileSizeBytes)})`}
        </Button>
      </Col>
    </div>
  );
}

OutputFileHeader.propTypes = {
  /*any extra attributes to add to PageHeader*/
  output: PropTypes.object.isRequired
};
