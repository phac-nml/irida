import React, { useEffect, useState } from "react";
import { Col, Modal, Row, Space, Typography } from "antd";
import {
  getUnreadAnnouncements,
  markAnnouncementRead,
} from "../../apis/announcements/announcements";
import { IconLeft, IconRight } from "../icons/Icons";
import { PriorityFlag } from "../../pages/announcement/components/PriorityFlag";
import Markdown from "react-markdown";
import { formatDate } from "../../utilities/date-utilities";

const { Text } = Typography;

export function Announcements() {
  const [announcements, setAnnouncements] = useState([]);
  const [announcement, setAnnouncement] = useState({});
  const [index, setIndex] = useState(0);
  const [visible, setVisibility] = useState(true);

  useEffect(() => {
    getUnreadAnnouncements().then((data) => {
      setAnnouncements(data.data);
      setAnnouncement(data.data[index]);
    });
  }, []);

  useEffect(() => {
    setAnnouncement(announcements[index]);
    console.log(announcement);
  }, [index]);

  const incrementIndex = () => {
    if (index == announcements.length - 1) {
      setIndex(0);
    } else {
      setIndex(index + 1);
    }
  };

  const decrementIndex = () => {
    if (index == 0) {
      setIndex(announcements.length - 1);
    } else {
      setIndex(index - 1);
    }
  };

  return (
    <>
      {announcement && announcement.user && (
        <Modal
          title={
            <>
              <Space>
                <PriorityFlag hasPriority={announcement.priority} />
                <Text strong>{announcement.title}</Text>
              </Space>
              <br />
              <Text type="secondary" style={{ fontSize: `.8em` }}>
                {i18n(
                  "ViewUnreadAnnouncement.create.details",
                  announcement.user.username,
                  formatDate({ date: announcement.createdDate })
                )}
              </Text>
            </>
          }
          onCancel={() => setVisibility(false)}
          visible={visible}
          width="60%"
          footer={null}
        >
          <Row justify="space-between" align="middle">
            <Col span={2} style={{ textAlign: "left" }}>
              <IconLeft onClick={decrementIndex} />
            </Col>
            <Col span={20}>
              <div key={announcement.identifier}>
                <div style={{ overflowY: "auto", maxHeight: 600 }}>
                  <Markdown source={announcement.message} />
                </div>
              </div>
            </Col>
            <Col span={2} style={{ textAlign: "right" }}>
              <IconRight onClick={incrementIndex} />
            </Col>
          </Row>
        </Modal>
      )}
    </>
  );
}
