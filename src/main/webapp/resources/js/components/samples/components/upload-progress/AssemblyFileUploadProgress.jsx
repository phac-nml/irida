import React from "react";
import { Collapse, List, Progress } from "antd";

const { Panel } = Collapse;
/**
 * React component to display assembly file upload progress
 *
 * @param assemblyFiles The list of assembly files that are uploading
 * @param assemblyProgress The progress of the upload
 * @returns {JSX.Element}
 * @constructor
 */
export function AssemblyFileUploadProgress({
  assemblyFiles,
  assemblyProgress,
}) {
  return (
    <div>
      {i18n("SampleFiles.uploadProgress", "Assembly")}
      :
      <Progress percent={assemblyProgress} />
      <Collapse>
        <Panel header={i18n("SampleFiles.filesUploading", "Assembly")} key="1">
          <List split={false}>
            {assemblyFiles.map((currFile, index) => {
              return (
                <List.Item key={`assembly-file-${index}`}>
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
