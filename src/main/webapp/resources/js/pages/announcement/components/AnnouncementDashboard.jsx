import React, { useEffect, useState } from "react";
import { Avatar, Empty, List, notification } from "antd";
import { fromNow } from "../../../utilities/date-utilities";
import {
  getUnreadAnnouncements,
  markAnnouncementRead,
} from "../../../apis/announcements/announcements";
import ViewUnreadAnnouncement from "./ViewUnreadAnnouncement";
import { PriorityFlag } from "./PriorityFlag";

/**
 * Component to display displays a list of unread announcements.
 * @returns {*}
 * @constructor
 */
export function AnnouncementDashboard() {
  const [unreadAnnouncements, setUnreadAnnouncements] = useState([]);

  useEffect(() => {
    getUnreadAnnouncements().then((data) => {
      setUnreadAnnouncements(data);
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
    <List
      locale={{
        emptyText: (
          <Empty
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description={i18n("AnnouncementDashboard.emptyList")}
          />
        ),
      }}
      pagination={unreadAnnouncements.length > 5 ? { pageSize: 5 } : false}
      dataSource={unreadAnnouncements}
      renderItem={(item) => (
        <List.Item className="t-announcement-item">
          <List.Item.Meta
            avatar=<Avatar
              style={{ backgroundColor: "#fff" }}
              icon={<PriorityFlag hasPriority={item.priority} />}
            />
            title=<ViewUnreadAnnouncement
              announcementID={item.identifier}
              announcementTitle={item.title}
              markAnnouncementAsRead={markAnnouncementAsRead}
            />
            description={fromNow({ date: item.createdDate })}
          />
        </List.Item>
      )}
    />
  );
}
