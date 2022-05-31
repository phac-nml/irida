import { Tag } from "antd";
import React from "react";

export default function UserStatusTag({ enabled }) {
  return enabled ? (
    <Tag color={"green"}>{i18n("UserDetails.enabled").toUpperCase()}</Tag>
  ) : (
    <Tag color={"red"}>DISABLED</Tag>
  );
}
