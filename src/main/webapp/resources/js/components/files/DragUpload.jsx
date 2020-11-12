import React from "react";
import { Upload } from "antd";
import { IconFileUpload } from "../icons/Icons";

const { Dragger } = Upload;

/**
 * React component for rendering the drag and drop upload functionality.
 * @returns {*}
 * @constructor
 */

export function DragUpload({ uploadText, uploadHint, ...uploadOptions }) {
  return (
    <Dragger {...uploadOptions}>
      <p className="ant-upload-drag-icon">
        <IconFileUpload />
      </p>
      <p className="ant-upload-text">{uploadText}</p>
      <p className="ant-upload-hint">{uploadHint}</p>
    </Dragger>
  );
}
