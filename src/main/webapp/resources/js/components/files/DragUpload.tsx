import React from "react";
import type { UploadProps } from "antd";
import { Upload } from "antd";
import { IconFileUpload } from "../icons/Icons";
import { SPACE_SM, SPACE_XS } from "../../styles/spacing";

const { Dragger } = Upload;

export interface Dictionary<T> {
  [Key: string]: T;
}

export interface DragUploadProps {
  uploadText: string | React.ReactElement;
  uploadHint: string | React.ReactElement;
  options: UploadProps;
  props: Dictionary<string>;
}
/**
 * React component for rendering the drag and drop upload functionality.
 *  @param uploadText - text for drag and drop
 *  @param uploadHint - hint for drag and drop
 *  @param options - upload options
 *  @param props - remainder of props passed
 * @returns {*}
 * @constructor
 */
export function DragUpload({
  uploadText,
  uploadHint,
  options,
  props,
}: DragUploadProps): JSX.Element {
  return (
    <div style={{ marginBottom: SPACE_SM }} {...props}>
      <Dragger {...options}>
        <p className="ant-upload-text" style={{ fontSize: "14px" }}>
          <IconFileUpload style={{ fontSize: "14px", marginRight: SPACE_XS }} />
          {uploadText}
        </p>
        <p className="ant-upload-hint" style={{ fontSize: "12px" }}>
          {uploadHint}
        </p>
      </Dragger>
    </div>
  );
}
