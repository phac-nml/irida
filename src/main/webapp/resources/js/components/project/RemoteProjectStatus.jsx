import React from "react";
import { Button, Tag } from "antd";
import { IconSwap } from "../icons/Icons";
import { SPACE_XS } from "../../styles/spacing";

export function RemoteProjectStatus() {
  const { remote } = window.project;
  if (!remote) return null;

  return window.project.canManageRemote ? (
    <Button size="small" icon={<IconSwap />} href={remote.url}>
      <strong style={{ marginLeft: SPACE_XS }}>{remote.label}</strong>:
      <span style={{ marginLeft: SPACE_XS }}>{remote.status}</span>
    </Button>
  ) : (
    <Tag icon={<IconSwap />}>
      <strong>{remote.label}</strong>: {remote.status}
    </Tag>
  );
}
