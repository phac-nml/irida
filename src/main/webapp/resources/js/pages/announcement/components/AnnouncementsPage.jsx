import React, { useEffect, useState } from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import {
  getAnnouncements,
  markAnnouncementRead,
} from "../../../apis/announcements/announcements";
import { Avatar, Col, Form, List, notification, Radio, Row } from "antd";
import { PriorityFlag } from "./PriorityFlag";
import { fromNow } from "../../../utilities/date-utilities";
import ViewReadAnnouncement from "./ViewReadAnnouncement";
import ViewUnreadAnnouncement from "./ViewUnreadAnnouncement";
import { grey2 } from "../../../styles/colors";

/**
 * React component to display the page for administration of users.
 * @returns {*}
 * @constructor
 */
export function AnnouncementsPage({}) {
  const [announcements, setAnnouncements] = useState([]);
  const [readToggle, setReadToggle] = useState(false);
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
  }, [filter, readToggle]);

  function markAnnouncementAsRead(aID) {
    return markAnnouncementRead({ aID })
      .then(() => {
        setReadToggle(!readToggle);
      })
      .catch(({ message }) => {
        notification.error({ message });
      });
  }

  return (
    <PageWrapper title="Announcements">
      <Row justify="center">
        <Col xs={24} sm={20} md={16} lg={12} xl={8}>
          <Form>
            <Form.Item>
              <Radio.Group
                value={filter}
                onChange={(e) => setFilter(e.target.value)}
              >
                <Radio.Button value="all">
                  {i18n("AnnouncementsPage.filter.all")}
                </Radio.Button>
                <Radio.Button value="unread">
                  {i18n("AnnouncementsPage.filter.unread")}
                </Radio.Button>
                <Radio.Button value="read">
                  {i18n("AnnouncementsPage.filter.read")}
                </Radio.Button>
              </Radio.Group>
            </Form.Item>
          </Form>
        </Col>
      </Row>
      <Row justify="center">
        <Col xs={24} sm={20} md={16} lg={12} xl={8}>
          <List
            bordered
            dataSource={announcements}
            pagination={true}
            renderItem={(item) => (
              <List.Item
                className="t-announcement-item"
                style={item.read ? { backgroundColor: grey2 } : {}}
              >
                <List.Item.Meta
                  avatar=<Avatar
                    size="small"
                    style={
                      item.read
                        ? { backgroundColor: grey2 }
                        : { backgroundColor: "#fff" }
                    }
                    icon={
                      <PriorityFlag hasPriority={item.announcement.priority} />
                    }
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
        </Col>
      </Row>
    </PageWrapper>
  );
}
