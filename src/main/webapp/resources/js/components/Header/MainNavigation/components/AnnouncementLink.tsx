/**
 * @file AnnouncementsSubMenu is the announcements drop down in the main navigation bar.
 */
import React from "react";
import { Avatar, Badge, Button, List, Popover } from "antd";
import { blue6, grey6 } from "../../../../styles/colors";
import { fromNow } from "../../../../utilities/date-utilities";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { IconFlag } from "../../../icons/Icons";
import { TYPES, useAnnouncements } from "./announcements-context";
import { BellOutlined } from "@ant-design/icons";

/**
 * React component to display the bell icon and new announcement count badge
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function AnnouncementLink(): JSX.Element {
  const [{ announcements }] = useAnnouncements();

  return (
    <Badge
      className="t-announcements-badge"
      count={announcements && announcements.filter((a) => !a.read).length}
      offset={[5, -3]}
    >
      <BellOutlined className="t-announcements-button" />
    </Badge>
  );
}
