import React, { useEffect, useState } from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import {
  getAnnouncements,
  markAnnouncementRead,
} from "../../../apis/announcements/announcements";
import { Avatar, Col, Empty, Form, List, notification, Radio, Row } from "antd";
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
  const [filteredAnnouncements, setFilteredAnnouncements] = useState([]);
  const [filter, setFilter] = useState("all");
  const [toggleRead, setToggleRead] = useState(false);

  useEffect(() => {
    getAnnouncements().then((data) => {
      setAnnouncements(data.data);
      setFilteredAnnouncementsOnToggle(filter, data.data);
    });
  }, [toggleRead]);

  useEffect(() => {
    setFilteredAnnouncementsOnToggle(filter, announcements);
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

  function setFilteredAnnouncementsOnToggle(filterOption, announcementsList) {
    switch (filterOption) {
      case "read":
        return setFilteredAnnouncements(
          announcementsList.filter((item) => item.announcementUserJoin !== null)
        );
      case "unread":
        return setFilteredAnnouncements(
          announcementsList.filter((item) => item.announcementUserJoin === null)
        );
      default:
        return setFilteredAnnouncements(announcementsList);
    }
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
                <Radio.Button value="all" className="t-all-announcements">
                  {i18n("AnnouncementsPage.filter.all")}
                </Radio.Button>
                <Radio.Button value="unread" className="t-unread-announcements">
                  {i18n("AnnouncementsPage.filter.unread")}
                </Radio.Button>
                <Radio.Button value="read" className="t-read-announcements">
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
            locale={{
              emptyText: (
                <Empty
                  image={Empty.PRESENTED_IMAGE_SIMPLE}
                  description={i18n("AnnouncementsPage.emptyList")}
                />
              ),
            }}
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
