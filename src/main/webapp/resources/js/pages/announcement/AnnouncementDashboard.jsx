import React, { useEffect, useState } from "react";
import { List, notification, Tabs, Typography } from "antd";
import { fromNow } from "../../utilities/date-utilities";
import {
  getUnreadAnnouncements,
  markAnnouncementRead,
} from "../../apis/announcements/announcements";
import { IconFlag } from "../../components/icons/Icons";
import { blue6, grey2 } from "../../styles/colors";
import ViewUnreadAnnouncement from "./ViewUnreadAnnouncement";
import Markdown from "react-markdown";

export function AnnouncementDashboard() {
  const [unreadAnnouncements, setUnreadAnnouncements] = useState([]);
  const [unreadTotal, setUnreadTotal] = useState(0);
  const { TabPane } = Tabs;
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
        <List.Item>
          <List.Item.Meta
            avatar={
              <IconFlag style={{ color: item.priority ? blue6 : grey2 }} />
            }
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
