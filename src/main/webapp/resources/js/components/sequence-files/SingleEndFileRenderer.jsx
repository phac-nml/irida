import React from "react";
import { List } from "antd";
import { SequenceFileHeader } from "./SequenceFileHeader";

export function SingleEndFileRenderer({ files }) {
  return (
    <List
      bordered
      dataSource={files}
      renderItem={(file) => (
        <List.Item>
          <SequenceFileHeader file={file} />
        </List.Item>
      )}
    />
  );
}
