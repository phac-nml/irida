import React from "react";
import { Avatar, List } from "antd";

export function SequenceFileDetailsRenderer({ file }) {
  return (
    <List.Item key={`file-${file.id}`} style={{ width: `100%` }}>
      <List.Item.Meta
        avatar={<Avatar size={`small`} icon={file.icon} />}
        title={file.label}
      />
    </List.Item>
  );
}
