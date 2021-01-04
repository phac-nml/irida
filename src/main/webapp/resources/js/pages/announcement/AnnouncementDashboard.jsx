import React, { useEffect, useState } from "react";
import { Button, Card, List, notification, Tabs, Typography } from "antd";
import { fromNow } from "../../utilities/date-utilities";
import {
  getReadAnnouncements,
  getUnreadAnnouncements,
  markAnnouncementRead,
} from "../../apis/announcements/announcements";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconFlag } from "../../components/icons/Icons";
import { blue6, grey2 } from "../../styles/colors";
import ViewReadAnnouncement from "./ViewReadAnnouncement";
import ViewUnreadAnnouncement from "./ViewUnreadAnnouncement";
import Markdown from "react-markdown";

export function AnnouncementDashboard() {
  const [readAnnouncements, setReadAnnouncements] = useState([]);
  const [unreadAnnouncements, setUnreadAnnouncements] = useState([]);
  const [unreadTotal, setUnreadTotal] = useState(0);
  const { TabPane } = Tabs;
  const { Paragraph } = Typography;

  useEffect(() => {
    getReadAnnouncements().then((data) => {
      setReadAnnouncements(data.data);
    });
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
              pagination={unreadTotal > 0 ? { pageSize: 5 } : false}
              dataSource={unreadAnnouncements}
              renderItem={(item) => (
                <List.Item>
                  <List.Item.Meta
                    avatar={
                      <IconFlag
                        style={{ color: item.priority ? blue6 : grey2 }}
                      />
                    }
                    title=<ViewUnreadAnnouncement
                      announcement={item}
                      markAnnouncementAsRead={markAnnouncementAsRead}
                    />
                    description={fromNow({ date: item.createdDate })}
                  />
                </List.Item>
              )}
            />
          </TabPane>
          <TabPane tab={"Read (" + readAnnouncements.length + ")"} key="2">
            <List
              pagination={{ pageSize: 5 }}
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
                  {/*<Paragraph*/}
                  {/*  ellipsis={{ rows: 2, expandable: true, symbol: "more" }}*/}
                  {/*>*/}
                  {/*  <Markdown source={item.subject.message} />*/}
                  {/*</Paragraph>*/}
                </List.Item>
              )}
            />
          </TabPane>
        </Tabs>
      </Card>
    </>
  );
}
