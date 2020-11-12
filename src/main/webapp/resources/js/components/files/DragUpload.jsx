import React from 'react';
import { Upload } from "antd";
import { SPACE_XS } from "../../styles/spacing";
import { IconFileUpload } from "../icons/Icons";

const { Dragger } = Upload;

/**
 * React component for rendering the drag and drop upload functionality.
 * @returns {*}
 * @constructor
 */

export function DragUpload(
  {...uploadOptions}
) {

  const uploadHintMessage = {
    true: i18n("ReferenceFile.singleOrMultiple"),
    false: "Supports single file upload"
  }

  return (
    <Dragger {...uploadOptions} style={{marginBottom: SPACE_XS}}>
      <p className="ant-upload-drag-icon">
        <IconFileUpload />
      </p>
      <p className="ant-upload-text">{i18n("ReferenceFile.clickorDrag")}</p>
      <p className="ant-upload-hint">
        {uploadHintMessage[uploadOptions.multiple]}
      </p>
    </Dragger>
  );
}