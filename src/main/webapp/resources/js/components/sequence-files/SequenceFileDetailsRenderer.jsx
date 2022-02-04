import React from "react";
import { Avatar, Button, List, Space } from "antd";
import { SPACE_XS } from "../../styles/spacing";
import { IconDownloadFile } from "../icons/Icons";

/**
 * React component to display paired end file details
 *
 * @param file The file to display details for
 * @param fileObjectId The sequencingobject identifier
 * @function download sequence file function
 * @function get file processing state function
 * @returns {JSX.Element}
 * @constructor
 */
export function SequenceFileDetailsRenderer({
  file,
  fileObjectId,
  downloadSequenceFile = () => {},
  getProcessingState = () => {},
}) {
  return (
    <List.Item key={`file-${file.id}`} style={{ width: `100%` }}>
      <List.Item.Meta
        avatar={<Avatar size={`small`} icon={file.icon} />}
        title={
          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <a href={file.fastqcLink} target="_blank" className="t-file-label">
              {file.label}
            </a>
            <Space direction="horizontal" size="small">
              {getProcessingState(file.processingState)}
              <span style={{ marginRight: SPACE_XS }} className="t-file-size">
                {file.filesize}
              </span>
              <Button
                shape="circle"
                icon={<IconDownloadFile />}
                className="t-download-file-btn"
                onClick={() => {
                  downloadSequenceFile({
                    sequencingObjectId: fileObjectId,
                    sequenceFileId: file.id,
                  });
                }}
              />
            </Space>
          </div>
        }
      />
    </List.Item>
  );
}
