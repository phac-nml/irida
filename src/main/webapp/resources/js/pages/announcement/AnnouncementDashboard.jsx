import React, { useEffect, useState } from "react";
import { List, notification, Typography } from "antd";
import { fromNow } from "../../utilities/date-utilities";
import {
  getUnreadAnnouncements,
  markAnnouncementRead,
} from "../../apis/announcements/announcements";
import ViewUnreadAnnouncement from "./ViewUnreadAnnouncement";
import Markdown from "react-markdown";
import { PriorityFlag } from "./PriorityFlag";

export function AnnouncementDashboard() {
  const [unreadAnnouncements, setUnreadAnnouncements] = useState([]);
  const [unreadTotal, setUnreadTotal] = useState(0);
  const { Paragraph } = Typography;

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
      pagination={unreadTotal > 5 ? { pageSize: 5 } : false}
      dataSource={unreadAnnouncements}
      renderItem={(item) => (
        <List.Item className="t-announcement-item">
          <List.Item.Meta
            avatar={<PriorityFlag hasPriority={item.priority} />}
            title=<ViewUnreadAnnouncement
              announcement={item}
              markAnnouncementAsRead={markAnnouncementAsRead}
            />
            description={fromNow({ date: item.createdDate })}
          />
          {/*<Paragraph*/}
          {/*  ellipsis={{ rows: 2, expandable: true, symbol: "more" }}*/}
          {/*>*/}
          {/*  <Markdown source={item.message} />*/}
          {/*</Paragraph>*/}
        </List.Item>
      )}
    />
  );
}
