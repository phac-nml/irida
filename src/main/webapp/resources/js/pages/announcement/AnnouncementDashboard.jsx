import React, { useEffect, useState } from "react";
import { Card, List } from "antd";
import { formatDate } from "../../utilities/date-utilities";
import {
  getReadAnnouncements,
  getUnreadAnnouncements,
} from "../../apis/announcements/announcements";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconFlag } from "../../components/icons/Icons";
import { blue6, grey2 } from "../../styles/colors";

export function AnnouncementDashboard() {
  const [readAnnouncements, setReadAnnouncements] = useState([]);
  const [unreadAnnouncements, setUnreadAnnouncements] = useState([]);

  useEffect(() => {
    getReadAnnouncements().then((data) => {
      console.log(data);
      setReadAnnouncements(data.data);
    });
    getUnreadAnnouncements().then((data) => {
      console.log(data);
      setUnreadAnnouncements(data.data);
    });
  }, []);

  return (
    <>
      <Card
        title={"Announcements (" + unreadAnnouncements.length + " Unread)"}
        extra={<a href={setBaseUrl("/announcements/user/read")}>View All</a>}
      >
        <List
          dataSource={unreadAnnouncements}
          renderItem={(item) => (
            <List.Item>
              <List.Item.Meta
                avatar={
                  <IconFlag style={{ color: item.priority ? blue6 : grey2 }} />
                }
                title={<span style={{ fontWeight: "bold" }}>{item.title}</span>}
                description={formatDate({ date: item.createdDate })}
              />
            </List.Item>
          )}
        />
        <List
          dataSource={readAnnouncements}
          renderItem={(item) => (
            <List.Item>
              <List.Item.Meta
                avatar={
                  <IconFlag
                    style={{ color: item.subject.priority ? blue6 : grey2 }}
                  />
                }
                title={
                  <span style={{ fontWeight: "normal" }}>
                    {item.subject.title}
                  </span>
                }
                description={formatDate({ date: item.subject.createdDate })}
              />
            </List.Item>
          )}
        />
      </Card>
    </>
  );
}
