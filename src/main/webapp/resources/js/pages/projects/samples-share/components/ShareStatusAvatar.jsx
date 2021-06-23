import { Avatar } from "antd";
import React from "react";
import { IconCheck, IconLocked } from "../../../../components/icons/Icons";
import { green6, grey1, grey8, yellow6 } from "../../../../styles/colors";

export function ShareStatusAvatar({ owner }) {
  return owner ? (
    <Avatar
      size="small"
      style={{ backgroundColor: green6 }}
      icon={<IconCheck style={{ color: grey1 }} />}
    />
  ) : (
    <Avatar
      size="small"
      style={{ backgroundColor: yellow6 }}
      icon={<IconLocked style={{ color: grey8 }} />}
    />
  );
}
