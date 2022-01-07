import React from "react";
import { Upload } from "antd";
import { IconFileUpload } from "../icons/Icons";
import { SPACE_SM } from "../../styles/spacing";

const { Dragger } = Upload;

/**
 * React component for rendering the drag and drop upload functionality.
 * @param {object} - upload options as well as text/hint for drag and drop
 * @returns {*}
 * @constructor
 */

export function DragUpload({ uploadText, uploadHint, options, ...props }) {
  return (
    <div style={{ marginBottom: SPACE_SM }} {...props}>
      <Dragger {...options}>
        <p className="ant-upload-drag-icon">
          <IconFileUpload style={{ fontSize: "24px" }} />
        </p>
        <p className="ant-upload-text" style={{ fontSize: "14px" }}>
          {uploadText}
        </p>
        <p className="ant-upload-hint" style={{ fontSize: "12px" }}>
          {uploadHint}
        </p>
      </Dragger>
    </div>
  );
}
