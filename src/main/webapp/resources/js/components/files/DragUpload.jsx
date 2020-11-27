import React from "react";
import { Upload } from "antd";
import { IconFileUpload } from "../icons/Icons";

const { Dragger } = Upload;

/**
 * React component for rendering the drag and drop upload functionality.
 * @param {object} - upload options as well as text/hint for drag and drop
 * @returns {*}
 * @constructor
 */

export function DragUpload({ uploadText, uploadHint, ...uploadOptions }) {
  return (
    /*
    Progress needs to be set to a string or number to keep it in the DOM.
    Otherwise there are warnings in console:
    'Warning: Invalid value for prop `$$typeof` on <div> tag. Either remove it from the
    element, or pass a string or number value to keep it in the DOM. For details,
    see https://reactjs.org/link/attribute-behavior'
     */
    <Dragger {...uploadOptions} progress={0}>
      <p className="ant-upload-drag-icon">
        <IconFileUpload />
      </p>
      <p className="ant-upload-text">{uploadText}</p>
      <p className="ant-upload-hint">{uploadHint}</p>
    </Dragger>
  );
}
