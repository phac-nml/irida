import React from "react";
import { Button, Descriptions, Modal, Space } from "antd";
import { IconEye } from "../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../styles/fonts";
import {
  useVisibility,
  VisibilityProvider,
} from "../../contexts/visibility-context";
import { formatDate } from "../../utilities/date-utilities";
import Markdown from "react-markdown";

/**
 * Component to add a button which will open a modal to view a read announcement.
 * @param {function} ViewReadAnnouncement - the function that displays a read announcement.
 * @returns {*}
 * @constructor
 */

function ViewReadAnnouncementModal({ announcement }) {
  const [visible, setVisibility] = useVisibility();

  return (
    <>
      <Button type="link" onClick={() => setVisibility(true)}>
        {announcement.subject.title}
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
            {announcement.subject.title}
          </Descriptions.Item>
          <Descriptions.Item label="Priority">
            {announcement.subject.priority ? "high" : "low"}
          </Descriptions.Item>
          <Descriptions.Item label="Created On">
            {formatDate({ date: announcement.subject.createdDate })}
          </Descriptions.Item>
          <Descriptions.Item label="Created By">
            {announcement.subject.user.username}
          </Descriptions.Item>
          <Descriptions.Item label="Message">
            <Markdown source={announcement.subject.message} />
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </>
  );
}

export default function ViewReadAnnouncement({ announcement }) {
  return (
    <VisibilityProvider>
      <ViewReadAnnouncementModal announcement={announcement} />
    </VisibilityProvider>
  );
}
