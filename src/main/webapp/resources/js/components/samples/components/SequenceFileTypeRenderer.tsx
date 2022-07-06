import React from "react";
import { Space, Typography } from "antd";

export interface SequenceFileTypeRendererProps {
  children: React.ReactNode;
  title: string
}

export function SequenceFileTypeRenderer({ children, title }: SequenceFileTypeRendererProps): JSX.Element {
  return (
    <Space direction={`vertical`} style={{ width: `100%` }}>
      <Typography.Text strong>{title}</Typography.Text>
      {children}
    </Space>
  );
}
