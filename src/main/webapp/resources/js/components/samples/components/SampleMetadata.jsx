import React from "react";
import { List } from "antd";

export function SampleMetadata({ metadata }) {
  return (
    <List
      itemLayout="horizontal"
      dataSource={Object.keys(metadata).sort((a, b) => a.localeCompare(b))}
      renderItem={(item) => (
        <List.Item>
          <List.Item.Meta title={item} description={metadata[item].value} />
        </List.Item>
      )}
    />
  );
}
