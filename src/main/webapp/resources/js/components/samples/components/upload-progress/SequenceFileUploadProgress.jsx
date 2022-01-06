import React from "react";
import { Collapse, List, Progress } from "antd";

const { Panel } = Collapse;

/**
 * React component to display sequence file upload progress
 *
 * @param fast5Files The list of sequence files that are uploading
 * @param fast5Progress The progress of the upload
 * @returns {JSX.Element}
 * @constructor
 */
export function SequenceFileUploadProgress({ sequenceFiles, seqFileProgress }) {
  return (
    <div>
      {i18n("SampleFiles.uploadProgress", "Sequence")}
      :
      <Progress percent={seqFileProgress} />
      <Collapse>
        <Panel header={i18n("SampleFiles.filesUploading", "Sequence")} key="1">
          <List split={false}>
            {sequenceFiles.map((currFile, index) => {
              return (
                <List.Item key={`seq-file-${index}`}>
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
