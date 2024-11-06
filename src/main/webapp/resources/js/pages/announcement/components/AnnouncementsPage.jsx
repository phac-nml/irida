import React, { useEffect, useState } from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import {
  getAnnouncements,
  markAnnouncementRead,
} from "../../../apis/announcements/announcements";
import {
  Avatar,
  Col,
  Empty,
  Form,
  List,
  notification,
  Radio,
  Row,
  Space,
} from "antd";
import { PriorityFlag } from "./PriorityFlag";
import { fromNow } from "../../../utilities/date-utilities";
import ViewAnnouncement from "./ViewAnnouncement";
import { grey2 } from "../../../styles/colors";

/**
 * React component to display the page for administration of users.
 * @returns {*}
 * @constructor
 */
export function AnnouncementsPage() {
  const [announcements, setAnnouncements] = useState([]);
  const [filteredAnnouncements, setFilteredAnnouncements] = useState([]);
  const [filter, setFilter] = useState("all");
  const [toggleRead, setToggleRead] = useState(false);

  useEffect(() => {
    getAnnouncements()
      .then((data) => {
        setAnnouncements(data);
        setFilteredAnnouncementsOnToggle(filter, data);
      })
      .catch(({ message }) => {
        notification.error({ message });
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
          announcementsList.filter((item) => item.read)
        );
      case "unread":
        return setFilteredAnnouncements(
          announcementsList.filter((item) => !item.read)
        );
      default:
        return setFilteredAnnouncements(announcementsList);
    }
  }

  return (
    <Row justify="center">
      <Col xs={24} sm={20} md={16} lg={12} xl={8}>
        <PageWrapper title="Announcements">
          <Space direction="vertical" style={{ width: "100%" }}>
            <Form>
              <Form.Item noStyle>
                <Radio.Group
                  value={filter}
                  onChange={(e) => setFilter(e.target.value)}
                >
                  <Radio.Button value="all" className="t-all-announcements">
                    {i18n("AnnouncementsPage.filter.all")}
                  </Radio.Button>
                  <Radio.Button
                    value="unread"
                    className="t-unread-announcements"
                  >
                    {i18n("AnnouncementsPage.filter.unread")}
                  </Radio.Button>
                  <Radio.Button value="read" className="t-read-announcements">
                    {i18n("AnnouncementsPage.filter.read")}
                  </Radio.Button>
                </Radio.Group>
              </Form.Item>
            </Form>
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
              pagination={
                filteredAnnouncements.length > 10 ? { pageSize: 10 } : false
              }
              renderItem={(item) => (
                <List.Item
                  className="t-announcement-item"
                  style={item.read ? {} : { backgroundColor: grey2 }}
                >
                  <List.Item.Meta
                    avatar=<Avatar
                      size="small"
                      style={
                        item.read
                          ? { backgroundColor: "#fff" }
                          : { backgroundColor: grey2 }
                      }
                      icon={<PriorityFlag hasPriority={item.priority} />}
                    />
                    title={
                      item.read ? (
                        <ViewAnnouncement
                          announcementID={item.announcementID}
                          announcementTitle={item.title}
                        />
                      ) : (
                        <ViewAnnouncement
                          announcementID={item.announcementID}
                          announcementTitle={item.title}
                          markAnnouncementAsRead={markAnnouncementAsRead}
                        />
                      )
                    }
                    description={fromNow({ date: item.createdDate })}
                  />
                </List.Item>
              )}
            />
          </Space>
        </PageWrapper>
      </Col>
    </Row>
  );
}
