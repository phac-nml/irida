import React from "react";
import { Modal, notification, Typography } from "antd";
import { VisibilityProvider } from "../../../contexts/visibility-context";
import { formatDate } from "../../../utilities/date-utilities";
import Markdown from "react-markdown";
import { LinkButton } from "../../../components/Buttons/LinkButton";
import { PriorityFlag } from "./PriorityFlag";
import { getAnnouncement } from "../../../apis/announcements/announcements";

const { Text } = Typography;
const { info } = Modal;

/**
 * Component to add a button which will open a modal to view a read announcement.
 * @param {long} announcementID - the announcement identifier.
 * @param {string} announcementTitle - the announcement title to be displayed in the link.
 * @returns {*}
 * @constructor
 */
function ViewReadAnnouncementModal({ announcementID, announcementTitle }) {
  function showAnnouncement(aID) {
    return getAnnouncement({ aID })
      .then((data) => {
        displayAnnouncement({ announcement: data });
      })
      .catch(({ message }) => {
        notification.error({ message });
      });
  }

  const displayAnnouncement = ({ announcement }) =>
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
        <div style={{ overflowY: "auto", maxHeight: 600, paddingRight: 10 }}>
          <Markdown source={announcement.message} />
        </div>
      ),
    });

  return (
    <>
      <LinkButton
        text={announcementTitle}
        onClick={() => showAnnouncement(announcementID)}
      />
    </>
  );
}

export default function ViewReadAnnouncement({
  announcementID,
  announcementTitle,
}) {
  return (
    <VisibilityProvider>
      <ViewReadAnnouncementModal
        announcementID={announcementID}
        announcementTitle={announcementTitle}
      />
    </VisibilityProvider>
  );
}
