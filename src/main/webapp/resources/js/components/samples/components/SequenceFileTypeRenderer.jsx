import React from "react";
import { Space, Typography } from "antd";

export function SequenceFileTypeRenderer({ children, title }) {
  return (
    <Space direction={`vertical`} style={{ width: `100%` }}>
      <Typography.Text strong>{title}</Typography.Text>
      {children}
    </Space>
  );
}
