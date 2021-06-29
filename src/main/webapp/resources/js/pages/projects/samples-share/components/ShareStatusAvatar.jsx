import { Avatar } from "antd";
import React from "react";
import { IconCheck, IconLocked } from "../../../../components/icons/Icons";
import { blue2, blue8, grey8, yellow6 } from "../../../../styles/colors";

export function ShareStatusAvatar({ remote, owner }) {
  return !remote && owner ? (
    <Avatar
      size="small"
      style={{ backgroundColor: blue2 }}
      icon={<IconCheck style={{ color: blue8 }} />}
    />
  ) : (
    <Avatar
      size="small"
      style={{ backgroundColor: yellow6 }}
      icon={<IconLocked style={{ color: grey8 }} />}
    />
  );
}
