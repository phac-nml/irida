import React from "react";
import { Typography, Space } from "antd";

export function SequenceFileTypeRenderer({ children, title }) {
  return (
    <Space direction={`vertical`} style={{ width: `100%` }}>
      <Typography.Text strong>{title}</Typography.Text>
      {children}
    </Space>
  );
}
