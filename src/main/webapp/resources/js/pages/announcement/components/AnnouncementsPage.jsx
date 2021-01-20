import React, { useEffect, useState } from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import {
  getAnnouncements,
  markAnnouncementRead,
} from "../../../apis/announcements/announcements";
import { Avatar, Form, List, notification, Radio, Typography } from "antd";
import { PriorityFlag } from "./PriorityFlag";
import { fromNow } from "../../../utilities/date-utilities";
import ViewReadAnnouncement from "./ViewReadAnnouncement";
import ViewUnreadAnnouncement from "./ViewUnreadAnnouncement";
import { blue1 } from "../../../styles/colors";

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
      console.log(data.data);
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

  function markAnnouncementAsRead(aID) {
    return markAnnouncementRead({ aID })
      .then(() => {
        setFilter("read");
      })
      .catch(({ message }) => {
        notification.error({ message });
      });
  }

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
          <List.Item
            className="t-announcement-item"
            style={
              item.read ? { backgroundColor: blue1, fontWeight: "bold" } : {}
            }
          >
            <List.Item.Meta
              avatar=<Avatar
                gap={10}
                style={
                  item.read
                    ? { backgroundColor: blue1 }
                    : { backgroundColor: "#fff" }
                }
                icon={<PriorityFlag hasPriority={item.announcement.priority} />}
              />
              title={
                item.read ? (
                  <ViewUnreadAnnouncement
                    announcement={item.announcement}
                    markAnnouncementAsRead={markAnnouncementAsRead}
                  />
                ) : (
                  <ViewReadAnnouncement announcement={item.announcement} />
                )
              }
              description={fromNow({ date: item.announcement.createdDate })}
            />
          </List.Item>
        )}
      />
    </PageWrapper>
  );
}
