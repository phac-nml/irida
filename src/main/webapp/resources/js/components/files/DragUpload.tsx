import React from "react";
import type { UploadProps } from "antd";
import { Upload } from "antd";
import { IconFileUpload } from "../icons/Icons";
import { SPACE_SM } from "../../styles/spacing";

const { Dragger } = Upload;

interface DragUploadProps extends React.HTMLAttributes<HTMLDivElement> {
  uploadText: string | React.ReactElement;
  uploadHint: string | React.ReactElement;
  options?: UploadProps;
}

/**
 * React component for rendering the drag and drop upload functionality.
 * @param uploadText - text for drag and drop
 * @param uploadHint - hint for drag and drop
 * @param options - upload options
 * @param props - remainder of props passed
 * @returns {*}
 * @constructor
 */

export function DragUpload({
  uploadText,
  uploadHint,
  options,
  ...props
}: DragUploadProps): JSX.Element {
  return (
    <div style={{ marginBottom: SPACE_SM }} {...props}>
      <Dragger {...options}>
        <p className="ant-upload-drag-icon">
          <IconFileUpload />
        </p>
        <p className="ant-upload-text">{uploadText}</p>
        <p className="ant-upload-hint" style={{ padding: SPACE_SM }}>
          {uploadHint}
        </p>
      </Dragger>
    </div>
  );
}
