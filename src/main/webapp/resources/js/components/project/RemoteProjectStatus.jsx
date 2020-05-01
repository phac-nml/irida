import React from "react";
import { Tag, Typography } from "antd";
import { IconSwap } from "../icons/Icons";

const { Text } = Typography;

export function RemoteProjectStatus() {
  const { remote } = window.project;
  if (!remote) return null;

  return (
    <Tag icon={<IconSwap />}>
      <a href={remote.url}>
        <Text strong>{remote.label}</Text>: {remote.status}
      </a>
    </Tag>
  );
}
