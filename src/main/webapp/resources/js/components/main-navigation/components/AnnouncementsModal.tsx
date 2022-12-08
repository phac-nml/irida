import React, { useEffect, useState } from "react";
import { Button, Space, Tag, Typography } from "antd";
import { formatDate } from "../../../utilities/date-utilities";
import {
  useGetUnreadAnnouncementsQuery,
  useMarkAnnouncementAsReadMutation,
} from "../../../redux/endpoints/announcements";
import ReactMarkdown from "react-markdown";
import { FlagTwoTone } from "@ant-design/icons";
import { ScrollableModal } from "../../ant.design/ScrollableModal";

const { Text } = Typography;

export default function AnnouncementsModal(): JSX.Element | null {
  const { data: announcements, isSuccess } = useGetUnreadAnnouncementsQuery(
    undefined,
    {}
  );
  const [markAnnouncementAsRead] = useMarkAnnouncementAsReadMutation();

  const [visible, setVisible] = useState<boolean>(false);
  const [index, setIndex] = useState<number>(0);

  useEffect(() => {
    if (isSuccess) {
      setVisible(true);
    }
  }, [isSuccess]);

  if (!isSuccess || announcements === undefined || announcements.length === 0) {
    return null;
  }

  const footer = visible && [
    index > 0 && (
      <Button
        key="previous_announcement"
        className="t-previous-announcement-button"
        onClick={() => {
          setIndex((prevState) => prevState - 1);
          markAnnouncementAsRead(announcements[index].id);
        }}
      >
        {i18n("AnnouncementsModal.previous")}
      </Button>
    ),
    (index === 0 || index + 1 === announcements.length) && (
      <Button
        key="close_announcement"
        className="t-close-announcement-button"
        onClick={() => {
          markAnnouncementAsRead(announcements[index].identifier);
          setVisible(false);
        }}
      >
        {i18n("AnnouncementsModal.close")}
      </Button>
    ),
    index + 1 < announcements.length && (
      <Button
        key="next_announcement"
        className="t-next-announcement-button"
        onClick={() => {
          markAnnouncementAsRead(announcements[index].identifier);
          setIndex((prevState) => prevState + 1);
        }}
      >
        {i18n("AnnouncementsModal.next")}
      </Button>
    ),
  ];

  return (
    <ScrollableModal
      className="t-announcements-modal"
      title={
        <Space direction="vertical" style={{ width: `100%` }}>
          <Tag className="t-read-over-unread-ratio">
            {i18n(
              "AnnouncementsModal.tag.details",
              index,
              announcements.length
            )}
          </Tag>
          <Space align="start">
            <FlagTwoTone
              twoToneColor={
                announcements[index].priority ? "#1890ff" : "#bfbfbf"
              }
            />
            <Space direction="vertical">
              <Text strong>{announcements[index].title}</Text>
              <Text type="secondary" style={{ fontSize: `.8em` }}>
                {i18n(
                  "AnnouncementsModal.create.details",
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
      onCancel={() => setVisible(false)}
      footer={footer}
    >
      <div style={{ marginLeft: "25px" }}>
        <ReactMarkdown>{announcements[index].message}</ReactMarkdown>
      </div>
    </ScrollableModal>
  );
}
