import React from "react";
import { LockOutlined } from "@ant-design/icons";
import { List, Space, Typography } from "antd";

export default function LockedSamplesList({ locked }) {
  return (
    <List
      size="small"
      bordered
      header={
        <Space>
          <LockOutlined />
          <Typography.Text>
            You do not have permission to modify these samples
          </Typography.Text>
        </Space>
      }
      dataSource={locked}
      renderItem={(sample) => (
        <List.Item>
          <List.Item.Meta title={sample.sampleName} />
        </List.Item>
      )}
    />
  );
}
