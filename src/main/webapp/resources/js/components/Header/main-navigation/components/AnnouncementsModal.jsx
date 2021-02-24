import React from "react";
import { ScrollableModal } from "../../../ant.design/ScrollableModal";
import { Button, Space, Tag, Typography } from "antd";
import { PriorityFlag } from "../../../../pages/announcement/components/PriorityFlag";
import { formatDate } from "../../../../utilities/date-utilities";
import Markdown from "react-markdown";
import { TYPES, useAnnouncements } from "./announcements-context";
import { displayNextAnnouncement } from "./announcement-dispatch";

const { Text } = Typography;

export function AnnouncementsModal() {
  const [
    { announcements, modalVisible: visible, index, isPriority },
    dispatch,
  ] = useAnnouncements();

  /*
 1. Read & Close
 > 1 Read && Previous && Read & Next
 Read & Close
   */
  const footer = [
    index > 0 && <Button key="previous">Previous</Button>,
    index + 1 < announcements.length && (
      <Button
        key="next"
        onClick={() => displayNextAnnouncement(dispatch, announcements[index])}
      >
        Next
      </Button>
    ),
    index + 1 === announcements.length && <Button key="close">Close</Button>,
  ];

  return visible && announcements.length ? (
    <ScrollableModal
      className="t-modal"
      closable={!isPriority}
      maskClosable={!isPriority}
      title={
        <Space direction="vertical" style={{ width: "100%" }}>
          <Tag className="t-read-over-unread-ratio">
            {`${announcements.filter((a) => a.read).length + 1} / ${
              announcements.length
            }`}
          </Tag>
          <Space align="start">
            <PriorityFlag hasPriority={announcements[index].priority} />
            <Space direction="vertical">
              <Text strong>{announcements[index].title}</Text>
              <Text type="secondary" style={{ fontSize: `.8em` }}>
                {i18n(
                  "AnnouncementsSubMenu.create.details",
                  announcements[index].user.username,
                  formatDate({ date: announcements[index].createdDate })
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
      <div style={{ marginLeft: "25px" }}>
        <Markdown source={announcements[index].message} />
      </div>
    </ScrollableModal>
  ) : null;
}
