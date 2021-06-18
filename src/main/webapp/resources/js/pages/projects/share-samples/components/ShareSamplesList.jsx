import { List } from "antd";
import React from "react";
import { ShareStatusAvatar } from "./ShareStatusAvatar";

export function ShareSamplesList({ samples }) {
  return (
    <List
      dataSource={samples}
      renderItem={(sample) => (
        <List.Item>
          <List.Item.Meta
            avatar={<ShareStatusAvatar owner={sample.owner} />}
            title={sample.label}
          />
        </List.Item>
      )}
    />
  );
}
