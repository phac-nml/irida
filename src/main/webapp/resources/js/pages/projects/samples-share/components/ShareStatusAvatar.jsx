import { Avatar } from "antd";
import React from "react";
import { IconCheck, IconLocked } from "../../../../components/icons/Icons";
import { blue2, blue8, grey8, yellow6 } from "../../../../styles/colors";

/**
 * React component to render the Avatar for the status of sharing a sample.
 * If the sample is remote or locked in it's current project, then it will
 * be locked in the destination project.
 *
 * @param {boolean} remote - is the sample from a remote project
 * @param {boolean} locked - is the sample locked in the current project
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareStatusAvatar({ remote, locked }) {
  return remote || locked ? (
    <Avatar
      size="small"
      style={{ backgroundColor: yellow6 }}
      icon={<IconLocked style={{ color: grey8 }} />}
    />
  ) : (
    <Avatar
      size="small"
      style={{ backgroundColor: blue2 }}
      icon={<IconCheck style={{ color: blue8 }} />}
    />
  );
}
