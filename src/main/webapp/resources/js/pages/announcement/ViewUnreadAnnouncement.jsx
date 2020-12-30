import React from "react";
import { Button, Descriptions, Modal, notification, Space } from "antd";
import { IconEye } from "../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../styles/fonts";
import {
  useVisibility,
  VisibilityProvider,
} from "../../contexts/visibility-context";
import { formatDate } from "../../utilities/date-utilities";
import Markdown from "react-markdown";
import { markAnnouncementRead } from "../../apis/announcements/announcements";

/**
 * Component to add a button which will open a modal to view a unread announcement.
 * @param {function} ViewAnnouncement - the function that displays a unread announcement.
 * @returns {*}
 * @constructor
 */

function ViewUnreadAnnouncementModal({ announcement }) {
  const [visible, setVisibility] = useVisibility();

  return (
    <>
      <Button type="link" onClick={() => setVisibility(true)}>
        {announcement.title}
      </Button>
      <Modal
        title={
          <Space>
            <IconEye style={{ color: FONT_COLOR_PRIMARY }} />
            {i18n("ViewAnnouncement.title")}
          </Space>
        }
        onCancel={() => setVisibility(false)}
        visible={visible}
        width={640}
        footer={null}
      >
        <Descriptions column={1} bordered={true}>
          <Descriptions.Item label="Title">
            {announcement.title}
          </Descriptions.Item>
          <Descriptions.Item label="Priority">
            {announcement.priority ? "high" : "low"}
          </Descriptions.Item>
          <Descriptions.Item label="Created On">
            {formatDate({ date: announcement.createdDate })}
          </Descriptions.Item>
          <Descriptions.Item label="Created By">
            {announcement.user.username}
          </Descriptions.Item>
          <Descriptions.Item label="Message">
            <Markdown source={announcement.message} />
          </Descriptions.Item>
        </Descriptions>
        <br />
        <Button
          type="primary"
          onClick={() => {
            markAnnouncementAsRead(announcement.identifier);
          }}
        >
          Mark as Read
        </Button>
      </Modal>
    </>
  );

  function markAnnouncementAsRead(aID) {
    markAnnouncementRead({ aID })
      .then(() => {
        setVisibility(false);
        // TODO: re-render the unread announcement list
      })
      .catch(({ message }) => {
        notification.error({ message });
      });
  }
}

export default function ViewUnreadAnnouncement({ announcement }) {
  return (
    <VisibilityProvider>
      <ViewUnreadAnnouncementModal announcement={announcement} />
    </VisibilityProvider>
  );
}
