import React, { useEffect, useState } from "react";
import { Card, Modal, notification, Tag, Typography } from "antd";
import {
  getUnreadAnnouncements,
  markAnnouncementRead,
} from "../../apis/announcements/announcements";
import Markdown from "react-markdown";
import { formatDate } from "../../utilities/date-utilities";

const { Text, Title } = Typography;

export function Announcements() {
  const [visible, setVisible] = useState(false);
  const [announcements, setAnnouncements] = useState();
  const [index, setIndex] = useState(0);

  useEffect(() => {
    let session_date_string = window.localStorage.getItem(
      "cancel-announcements-read-date"
    );
    if (session_date_string) {
      let session_date = new Date(session_date_string);
      let today = new Date();

      if (
        !(
          session_date.getFullYear() === today.getFullYear() &&
          session_date.getMonth() === today.getMonth() &&
          session_date.getDate() === today.getDate()
        )
      ) {
        // dates are NOT in the same day
        getUnreadAnnouncements({ params: { priority: true } }).then(
          ({ data }) => {
            if (data.length) {
              setAnnouncements(data);
              setVisible(true);
            }
          }
        );
      }
    }
  }, []);

  function markAnnouncementAsRead(aID) {
    markAnnouncementRead({ aID })
      .then(() => {
        setIndex(index + 1);
      })
      .catch(({ message }) => {
        notification.error({ message });
      });
  }

  const onOk = () => {
    markAnnouncementAsRead(announcements[index].identifier);

    if (index + 1 === announcements.length) {
      setVisible(false);
    }
  };

  const onCancel = () => {
    window.localStorage.setItem("cancel-announcements-read-date", Date());
    setVisible(false);
  };

  return announcements && visible ? (
    <Modal
      className="t-modal"
      closable={false}
      maskClosable={false}
      title={
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          {announcements.length > 1
            ? i18n("Announcements.title.multiple", announcements.length)
            : i18n("Announcements.title.single")}
          <Tag className="t-read-over-unread-ratio" color="red">
            {index + 1} / {announcements.length}
          </Tag>
        </div>
      }
      visible={visible}
      width="60%"
      okText={i18n("Announcements.ok")}
      onOk={onOk}
      onCancel={onCancel}
    >
      <Card
        title={
          <>
            <Title level={5}>{announcements[index].title}</Title>
            <Text type="secondary" style={{ fontSize: `.8em` }}>
              {i18n(
                "Announcements.create.details",
                announcements[index].user.username,
                formatDate({ date: announcements[index].createdDate })
              )}
            </Text>
          </>
        }
      >
        <div style={{ overflowY: "auto", maxHeight: 600, paddingRight: 10 }}>
          <Markdown source={announcements[index].message} />
        </div>
      </Card>
    </Modal>
  ) : null;
}
