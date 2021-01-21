import React from "react";
import { Modal, Typography } from "antd";
import { VisibilityProvider } from "../../../contexts/visibility-context";
import { formatDate } from "../../../utilities/date-utilities";
import Markdown from "react-markdown";
import { LinkButton } from "../../../components/Buttons/LinkButton";
import { PriorityFlag } from "./PriorityFlag";

const { Text } = Typography;
const { info } = Modal;

/**
 * Component to add a button which will open a modal to view a unread announcement.
 * @param {object} announcement - the announcement that is to be displayed.
 * @param {boolean} isRead - whether the announcement has been read.
 * @param {function} markAnnouncementAsRead - the function that marks the announcement as read.
 * @returns {*}
 * @constructor
 */

function ViewReadAnnouncementModal({ announcement }) {
  const displayAnnouncement = () =>
    info({
      type: "info",
      width: `60%`,
      icon: <PriorityFlag hasPriority={announcement.priority} />,
      title: (
        <>
          <Text strong>{announcement.title}</Text>
          <br />
          <Text type="secondary" style={{ fontSize: `.8em` }}>
            {i18n(
              "ViewReadAnnouncement.details",
              announcement.user.username,
              formatDate({
                date: announcement.createdDate,
              })
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
export default function ViewReadAnnouncement({ announcement }) {
  return (
    <VisibilityProvider>
      <ViewReadAnnouncementModal announcement={announcement} />
    </VisibilityProvider>
  );
}
