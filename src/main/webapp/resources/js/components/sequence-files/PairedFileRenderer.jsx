import React from "react";
import { List, Typography } from "antd";
import { ArrowLeftOutlined, ArrowRightOutlined } from "@ant-design/icons";
import { SequenceFileDetailsRenderer } from "./SequenceFileDetailsRenderer";
import { SequenceFileHeader } from "./SequenceFileHeader";

const { Text } = Typography;

export function PairedFileRenderer({ pair }) {
  const files = [
    {
      label: pair.forwardSequenceFile.label,
      id: pair.forwardSequenceFile.identifier,
      icon: <ArrowRightOutlined />,
    },
    {
      label: pair.reverseSequenceFile.label,
      id: pair.reverseSequenceFile.identifier,
      icon: <ArrowLeftOutlined />,
    },
  ];

  return (
    <List
      bordered
      header={<SequenceFileHeader file={pair} />}
      layout={`vertical`}
      dataSource={files}
      renderItem={(file) => <SequenceFileDetailsRenderer file={file} />}
    />
  );
}
