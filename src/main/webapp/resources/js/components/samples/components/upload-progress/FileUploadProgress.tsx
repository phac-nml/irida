import React from "react";
import { Collapse, List, Progress } from "antd";

const { Panel } = Collapse;

export interface FileProps {
  uid: string;
  lastModified: number;
  lastModifiedDate: Date;
  name: string;
  size: number;
  type: string;
  webkitRelativePath: string;
}

export interface FileUploadProgressProps {
  files: FileProps[];
  uploadProgress: number;
  type: string;
}

/**
 * React component to display file upload progress
 *
 * @param files The list of files that are uploading
 * @param uploadProgress The progress of the upload
 * @param type The type of file upload
 * @returns {JSX.Element}
 * @constructor
 */
export function FileUploadProgress({
  files,
  uploadProgress,
  type,
}: FileUploadProgressProps): JSX.Element {
  console.log(files);
  return (
    <div>
      {i18n("SampleFiles.uploadProgress", type)}
      :
      <Progress percent={uploadProgress} />
      <Collapse>
        <Panel header={i18n("SampleFiles.filesUploading", type)} key="1">
          <List split={false}>
            {files.map((currFile, index) => {
              return (
                <List.Item key={`${type}-file-${index}`}>
                  - {currFile.name}
                </List.Item>
              );
            })}
          </List>
        </Panel>
      </Collapse>
    </div>
  );
}
