import React, { useEffect, useState } from "react";
import { Button, Card, List, Tabs } from "antd";
import { fromNow } from "../../utilities/date-utilities";
import {
  getReadAnnouncements,
  getUnreadAnnouncements,
} from "../../apis/announcements/announcements";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconFlag } from "../../components/icons/Icons";
import { blue6, grey2 } from "../../styles/colors";
import ViewReadAnnouncement from "./ViewReadAnnouncement";
import ViewUnreadAnnouncement from "./ViewUnreadAnnouncement";

export function AnnouncementDashboard() {
  const [readAnnouncements, setReadAnnouncements] = useState([]);
  const [unreadAnnouncements, setUnreadAnnouncements] = useState([]);
  const { TabPane } = Tabs;

  useEffect(() => {
    getReadAnnouncements().then((data) => {
      setReadAnnouncements(data.data);
    });
    getUnreadAnnouncements().then((data) => {
      setUnreadAnnouncements(data.data);
    });
  }, []);

  return (
    <>
      <Card
        title="Announcements"
        extra={
          <Button
            type="primary"
            ghost
            href={setBaseUrl("/announcements/user/read")}
          >
            View All
          </Button>
        }
      >
        <Tabs defaultActiveKey="1">
          <TabPane tab={"Unread (" + unreadAnnouncements.length + ")"} key="1">
            <List
              dataSource={unreadAnnouncements}
              renderItem={(item) => (
                <List.Item>
                  <List.Item.Meta
                    avatar={
                      <IconFlag
                        style={{ color: item.priority ? blue6 : grey2 }}
                      />
                    }
                    title=<ViewUnreadAnnouncement announcement={item} />
                    description={fromNow({ date: item.createdDate })}
                  />
                </List.Item>
              )}
            />
          </TabPane>
          <TabPane tab={"Read (" + readAnnouncements.length + ")"} key="2">
            <List
              itemLayout="vertical"
              dataSource={readAnnouncements}
              renderItem={(item) => (
                <List.Item>
                  <List.Item.Meta
                    avatar={
                      <IconFlag
                        style={{ color: item.subject.priority ? blue6 : grey2 }}
                      />
                    }
                    title=<ViewReadAnnouncement announcement={item} />
                    description={fromNow({ date: item.subject.createdDate })}
                  />
                </List.Item>
              )}
            />
          </TabPane>
        </Tabs>
      </Card>
    </>
  );
}
