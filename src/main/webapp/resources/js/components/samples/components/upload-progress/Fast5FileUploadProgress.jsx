import React from "react";
import { Collapse, List, Progress } from "antd";

const { Panel } = Collapse;
/**
 * React component to display fast5 file upload progress
 *
 * @param fast5Files The list of fast5 files that are uploading
 * @param fast5Progress The progress of the upload
 * @returns {JSX.Element}
 * @constructor
 */
export function Fast5FileUploadProgress({ fast5Files, fast5Progress }) {
  return (
    <div>
      {i18n("SampleFiles.uploadProgress", "Fast5")}
      :
      <Progress percent={fast5Progress} />
      <Collapse>
        <Panel header={i18n("SampleFiles.filesUploading", "Fast5")} key="1">
          <List split={false}>
            {fast5Files.map((currFile, index) => {
              return (
                <List.Item key={`fast5-file-${index}`}>
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
