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
  const [filteredAnnouncements, setFilteredAnnouncements] = useState([]);
  const [allAnnouncements, setAllAnnouncements] = useState([]);
  const [readAnnouncements, setReadAnnouncements] = useState([]);
  const [unreadAnnouncements, setUnreadAnnouncements] = useState([]);
  const [filter, setFilter] = useState("all");
  const [toggleRead, setToggleRead] = useState("all");

  useEffect(() => {
    getAnnouncements().then((data) => {
      setFilteredAnnouncements(data.data);
      setAllAnnouncements(data.data);
      setReadAnnouncements(
        data.data.filter((item) => item.announcementUserJoin !== null)
      );
      setUnreadAnnouncements(
        data.data.filter((item) => item.announcementUserJoin === null)
      );
    });
  }, [toggleRead]);

  useEffect(() => {
    switch (filter) {
      case "read":
        return setFilteredAnnouncements(readAnnouncements);
      case "unread":
        return setFilteredAnnouncements(unreadAnnouncements);
      default:
        return setFilteredAnnouncements(allAnnouncements);
    }
  }, [filter]);

  function markAnnouncementAsRead(aID) {
    return markAnnouncementRead({ aID })
      .then(() => {
        setToggleRead(!toggleRead);
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
            dataSource={filteredAnnouncements}
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
