import React, { useEffect, useState } from "react";
import { Badge, Menu, notification } from "antd";
import { IconBell } from "../../../icons/Icons";
import {
  getUnreadAnnouncements,
  markAnnouncementRead,
} from "../../../../apis/announcements/announcements";
import ViewAnnouncement from "../../../../pages/announcement/components/ViewAnnouncement";

/**
 * React component to display the bell icon and new announcement count badge
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function AnnouncementsSubMenu({ ...props }) {
  const [count, setCount] = React.useState(0);
  const [unreadAnnouncements, setUnreadAnnouncements] = useState([]);

  useEffect(() => {
    getUnreadAnnouncements({}).then(({ data }) => {
      setUnreadAnnouncements(data);
      setCount(data.length);
    });
  }, []);

  function markAnnouncementAsRead(aID) {
    return markAnnouncementRead({ aID })
      .then(() => {
        setUnreadAnnouncements(
          unreadAnnouncements.filter((item) => item.identifier !== aID)
        );
      })
      .catch(({ message }) => {
        notification.error({ message });
      });
  }

  return (
    <Menu.SubMenu
      title={
        <Badge count={count}>
          <IconBell />
        </Badge>
      }
      {...props}
    >
      {unreadAnnouncements.map((item, index) => (
        <Menu.Item key={"announcement_" + index}>
          <ViewAnnouncement
            announcementID={item.identifier}
            announcementTitle={item.title}
            markAnnouncementAsRead={markAnnouncementAsRead}
          />
        </Menu.Item>
      ))}
    </Menu.SubMenu>
  );
}
