import React from "react";
import { Button, Modal, Space, Typography } from "antd";
import { IconFlag } from "../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../styles/fonts";
import {
  useVisibility,
  VisibilityProvider,
} from "../../contexts/visibility-context";
import { formatDate } from "../../utilities/date-utilities";
import Markdown from "react-markdown";

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
      <Button type="link" onClick={() => setVisibility(true)}>
        {announcement.title}
      </Button>
      <Modal
        title={
          <Space>
            {announcement.title}
            {announcement.priority && (
              <IconFlag style={{ color: FONT_COLOR_PRIMARY }} />
            )}
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
