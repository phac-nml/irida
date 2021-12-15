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
 * @returns {JSX.Element}
 * @constructor
 */
export function SequenceFileDetailsRenderer({
  file,
  fileObjectId,
  downloadSequenceFile = () => {},
}) {
  return (
    <List.Item key={`file-${file.id}`} style={{ width: `100%` }}>
      <List.Item.Meta
        avatar={<Avatar size={`small`} icon={file.icon} />}
        title={
          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <a href={file.fastqcLink} target="_blank">
              {file.label}
            </a>
            <Space direction="horizontal" size="small">
              <span style={{ marginRight: SPACE_XS }}>{file.filesize}</span>
              <Button
                shape="circle"
                icon={<IconDownloadFile />}
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
