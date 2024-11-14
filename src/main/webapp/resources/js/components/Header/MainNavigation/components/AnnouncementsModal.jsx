import React from "react";
import { ScrollableModal } from "../../../ant.design/ScrollableModal";
import { Button, Space, Tag, Typography } from "antd";
import { PriorityFlag } from "../../../../pages/announcement/components/PriorityFlag";
import { formatDate } from "../../../../utilities/date-utilities";
import { micromark } from "micromark";
import { TYPES, useAnnouncements } from "./announcements-context";
import {
  readAndCloseAnnouncement,
  readAndNextAnnouncement,
  readAndPreviousAnnouncement,
} from "./announcement-dispatch";

const { Text } = Typography;

/**
 * React component to display the announcements modal.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function AnnouncementsModal() {
  const [
    { announcements, modalVisible: visible, index, isPriority },
    dispatch,
  ] = useAnnouncements();

  const [newAnnouncements, setNewAnnouncements] = React.useState([]);

  React.useEffect(() => {
    if (isPriority) {
      setNewAnnouncements(announcements.filter((a) => a.priority));
    } else {
      setNewAnnouncements(announcements);
    }
  }, [announcements]);

  const footer = [
    index > 0 && (
      <Button
        key="previous_announcement"
        className="t-previous-announcement-button"
        onClick={() =>
          readAndPreviousAnnouncement(dispatch, newAnnouncements[index])
        }
      >
        {i18n("AnnouncementsModal.previous")}
      </Button>
    ),
    (index === 0 || index + 1 === newAnnouncements.length) && (
      <Button
        key="close_announcement"
        className="t-close-announcement-button"
        onClick={() =>
          readAndCloseAnnouncement(dispatch, newAnnouncements[index])
        }
      >
        {i18n("AnnouncementsModal.close")}
      </Button>
    ),
    index + 1 < newAnnouncements.length && (
      <Button
        key="next_announcement"
        className="t-next-announcement-button"
        onClick={() =>
          readAndNextAnnouncement(dispatch, newAnnouncements[index])
        }
      >
        {i18n("AnnouncementsModal.next")}
      </Button>
    ),
  ];

  return visible && newAnnouncements.length ? (
    <ScrollableModal
      className="t-announcements-modal"
      closable={!isPriority}
      maskClosable={!isPriority}
      title={
        <Space direction="vertical" style={{ display: "block" }}>
          <Tag className="t-read-over-unread-ratio">
            {i18n(
              "AnnouncementsModal.tag.details",
              newAnnouncements.filter((a) => a.read).length,
              newAnnouncements.length
            )}
          </Tag>
          <Space align="start">
            <PriorityFlag hasPriority={newAnnouncements[index].priority} />
            <Space direction="vertical">
              <Text strong>{newAnnouncements[index].title}</Text>
              <Text type="secondary" style={{ fontSize: `.8em` }}>
                {i18n(
                  "AnnouncementsModal.create.details",
                  newAnnouncements[index].user.username,
                  formatDate({ date: newAnnouncements[index].createdDate })
                )}
              </Text>
            </Space>
          </Space>
        </Space>
      }
      visible={visible}
      width="90ch"
      onCancel={() => dispatch({ type: TYPES.CLOSE_ANNOUNCEMENT })}
      footer={footer}
    >
      <div 
        style={{ marginLeft: "25px" }}
        dangerouslySetInnerHTML={{
          __html: micromark(newAnnouncements[index].message)
        }}
      />
    </ScrollableModal>
  ) : null;
}
