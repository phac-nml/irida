import React from "react";
import { WarningOutlined } from "@ant-design/icons";
import { List, Space, Typography } from "antd";

export default function AssociatedSamplesList({ associated }) {
  return (
    <List
      size="small"
      bordered
      header={
        <Space>
          <WarningOutlined />
          <Typography.Text>
            These samples are from an associated project and cannot be removed
            from there
          </Typography.Text>
        </Space>
      }
      dataSource={associated}
      renderItem={(sample) => (
        <List.Item>
          <List.Item.Meta title={sample.sampleName} />
        </List.Item>
      )}
    />
  );
}
