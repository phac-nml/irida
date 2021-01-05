import React from "react";
import { Modal, Space, Typography } from "antd";
import {
  useVisibility,
  VisibilityProvider,
} from "../../contexts/visibility-context";
import { formatDate } from "../../utilities/date-utilities";
import Markdown from "react-markdown";
import { LinkButton } from "../../components/Buttons/LinkButton";
import { PriorityFlag } from "./PriorityFlag";

/**
 * Component to add a button which will open a modal to view a unread announcement.
 * @param {function} ViewAnnouncement - the function that displays a unread announcement.
 * @returns {*}
 * @constructor
 */

const { Text } = Typography;

function ViewUnreadAnnouncementModal({ announcement, markAnnouncementAsRead }) {
  const [visible, setVisibility] = useVisibility();

  return (
    <>
      <LinkButton
        text={announcement.title}
        onClick={() => setVisibility(true)}
      />
      <Modal
        title={
          <Space>
            <PriorityFlag hasPriority={announcement.priority} />
            {announcement.title}
          </Space>
        }
        afterClose={() => markAnnouncementAsRead(announcement.identifier)}
        onCancel={() => setVisibility(false)}
        visible={visible}
        width={640}
        footer={null}
      >
        <Markdown source={announcement.message} />
        <br />
        <Text type="secondary">
          Created by {announcement.user.username} on{" "}
          {formatDate({ date: announcement.createdDate })}
        </Text>
      </Modal>
    </>
  );
}

export default function ViewUnreadAnnouncement({
  announcement,
  markAnnouncementAsRead,
}) {
  return (
    <VisibilityProvider>
      <ViewUnreadAnnouncementModal
        announcement={announcement}
        markAnnouncementAsRead={markAnnouncementAsRead}
      />
    </VisibilityProvider>
  );
}
