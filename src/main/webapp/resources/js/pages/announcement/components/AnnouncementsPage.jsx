import React, { useEffect, useState } from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { getAnnouncements } from "../../../apis/announcements/announcements";
import { Avatar, Form, List, Radio, Typography } from "antd";
import { PriorityFlag } from "./PriorityFlag";
import { formatDate } from "../../../utilities/date-utilities";

const { Text } = Typography;

/**
 * React component to display the page for administration of users.
 * @returns {*}
 * @constructor
 */
export function AnnouncementsPage({}) {
  const [announcements, setAnnouncements] = useState([]);
  const [filter, setFilter] = useState("all");

  useEffect(() => {
    getAnnouncements().then((data) => {
      switch (filter) {
        case "read":
          return setAnnouncements(
            data.data.filter((item) => item.announcementUserJoin !== null)
          );
        case "unread":
          return setAnnouncements(
            data.data.filter((item) => item.announcementUserJoin === null)
          );
        default:
          return setAnnouncements(data.data);
      }
    });
  }, [filter]);

  return (
    <PageWrapper title="Announcements">
      <Form>
        <Form.Item>
          <Radio.Group
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
          >
            <Radio.Button value="all">All</Radio.Button>
            <Radio.Button value="unread">Unread</Radio.Button>
            <Radio.Button value="read">Read</Radio.Button>
          </Radio.Group>
        </Form.Item>
      </Form>
      <List
        dataSource={announcements}
        renderItem={(item) => (
          <List.Item className="t-announcement-item">
            <List.Item.Meta
              avatar=<Avatar
                style={{ backgroundColor: "#fff" }}
                icon={<PriorityFlag hasPriority={item.announcement.priority} />}
              />
              title=<Text strong={item.announcementUserJoin ? false : true}>
                {item.announcement.title}
              </Text>
              description={formatDate({ date: item.announcement.createdDate })}
            />
          </List.Item>
        )}
      />
    </PageWrapper>
  );
}
