import React from "react";
import { Modal, Typography } from "antd";
import { VisibilityProvider } from "../../../contexts/visibility-context";
import { formatDate } from "../../../utilities/date-utilities";
import Markdown from "react-markdown";
import { LinkButton } from "../../../components/Buttons/LinkButton";
import { PriorityFlag } from "./PriorityFlag";

/**
 * Component to add a button which will open a modal to view a unread announcement.
 * @param {function} ViewAnnouncement - the function that displays a unread announcement.
 * @returns {*}
 * @constructor
 */
const { Text } = Typography;
const { confirm } = Modal;
function ViewUnreadAnnouncementModal({ announcement, markAnnouncementAsRead }) {
  const displayAnnouncement = () =>
    confirm({
      type: "info",
      width: `60%`,
      icon: <PriorityFlag hasPriority={announcement.priority} />,
      okText: `Read`,
      onOk() {
        markAnnouncementAsRead(announcement.identifier);
      },
      title: (
        <>
          <Text strong>{announcement.title}</Text>
          <br />
          <Text type="secondary" style={{ fontSize: `.8em` }}>
            {i18n(
              "ViewUnreadAnnouncement.create.details",
              announcement.user.username,
              formatDate({ date: announcement.createdDate })
            )}
          </Text>
        </>
      ),
      content: (
        <div style={{ overflowY: "auto", maxHeight: 600 }}>
          <Markdown source={announcement.message} />
        </div>
      ),
    });
  return (
    <>
      <LinkButton text={announcement.title} onClick={displayAnnouncement} />
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
