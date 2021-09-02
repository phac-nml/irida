import React from "react";
import { Avatar, Button, List } from "antd";
import { SPACE_XS } from "../../styles/spacing";
import { IconDownloadFile, IconRemove } from "../icons/Icons";

export function SequenceFileDetailsRenderer({ file }) {
  return (
    <List.Item key={`file-${file.id}`} style={{ width: `100%` }}>
      <List.Item.Meta
        avatar={<Avatar size={`small`} icon={file.icon} />}
        title={
          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <a href={file.fastqcLink} target="_blank">
              {file.label}
            </a>
            <span>
              <span style={{ marginRight: SPACE_XS }}>{file.filesize}</span>
              <Button
                style={{ marginRight: SPACE_XS }}
                shape="circle"
                icon={<IconDownloadFile />}
              />
              <Button shape="circle" icon={<IconRemove />} />
            </span>
          </div>
        }
      />
    </List.Item>
  );
}
