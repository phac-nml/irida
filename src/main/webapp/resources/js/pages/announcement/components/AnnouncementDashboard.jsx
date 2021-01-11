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
  const [unreadTotal, setUnreadTotal] = useState(0);

  useEffect(() => {
    getUnreadAnnouncements().then((data) => {
      setUnreadAnnouncements(data.data);
      setUnreadTotal(data.data.length);
    });
  }, [unreadTotal]);

  function markAnnouncementAsRead(aID) {
    return markAnnouncementRead({ aID })
      .then(() => {
        setUnreadTotal(unreadTotal - 1);
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
      pagination={unreadTotal > 5 ? { pageSize: 5 } : false}
      dataSource={unreadAnnouncements}
      renderItem={(item) => (
        <List.Item className="t-announcement-item">
          <List.Item.Meta
            avatar=<Avatar
              style={{ backgroundColor: "#fff" }}
              icon={<PriorityFlag hasPriority={item.priority} />}
            />
            title=<ViewUnreadAnnouncement
              announcement={item}
              markAnnouncementAsRead={markAnnouncementAsRead}
            />
            description={fromNow({ date: item.createdDate })}
          />
        </List.Item>
      )}
    />
  );
}
