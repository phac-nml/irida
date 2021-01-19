import React, { useEffect, useState } from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { getAnnouncements } from "../../../apis/announcements/announcements";
import { List } from "antd";
import { fromNow } from "../../../utilities/date-utilities";

/**
 * React component to display the page for administration of users.
 * @returns {*}
 * @constructor
 */
export function AnnouncementsPage({}) {
  const [announcements, setAnnouncements] = useState([]);

  useEffect(() => {
    console.log(window.PAGE.announcements);
    getAnnouncements().then((data) => {
      setAnnouncements(data.data);
      console.log(data.data);
    });
  }, []);

  return (
    <PageWrapper title="Announcements">
      <List
        dataSource={announcements}
        renderItem={(item) => (
          <List.Item className="t-announcement-item">
            <List.Item.Meta>
              title={item.announcement.title}
              description={fromNow({ date: item.announcement.createdDate })}
            </List.Item.Meta>
            {item.announcement.message}
          </List.Item>
        )}
      />
    </PageWrapper>
  );
}
