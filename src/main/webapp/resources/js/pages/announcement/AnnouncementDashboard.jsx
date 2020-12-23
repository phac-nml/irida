import React, { useEffect, useState } from "react";
import { Card, List } from "antd";
import { formatDate } from "../../utilities/date-utilities";
import { getUnreadAnnouncements } from "../../apis/announcements/announcements";

export function AnnouncementDashboard() {
  const [unreadAnnouncements, setUnreadAnnouncements] = useState([]);

  useEffect(() => {
    getUnreadAnnouncements().then((data) => {
      console.log(data);
      setUnreadAnnouncements(data.data);
    });
  }, []);

  return (
    <>
      <Card title="New Announcements">
        <List
          dataSource={unreadAnnouncements}
          renderItem={(item) => (
            <List.Item>
              <List.Item.Meta
                title={item.title}
                description={formatDate({ date: item.createdDate })}
              />
            </List.Item>
          )}
        />
      </Card>
    </>
  );
}
