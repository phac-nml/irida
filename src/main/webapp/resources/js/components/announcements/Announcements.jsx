import React, { useEffect, useState } from "react";
import { Col, Modal, Row, Space, Typography } from "antd";
import {
  getUnreadAnnouncements,
  markAnnouncementRead,
} from "../../apis/announcements/announcements";
import { IconLeft, IconRight } from "../icons/Icons";
import { PriorityFlag } from "../../pages/announcement/components/PriorityFlag";
import Markdown from "react-markdown";
import { formatDate } from "../../utilities/date-utilities";

const { Text } = Typography;
const { confirm } = Modal;
export function Announcements() {
  const [announcements, setAnnouncements] = useState();
  const [index, setIndex] = useState(0);

  useEffect(() => {
    getUnreadAnnouncements().then((data) => {
      console.log(data.data);
      if (data.data) {
        setAnnouncements(data.data);
      }
    });
  }, []);

  useEffect(() => {
    console.log("HEEEEEEELLLLLLLOOOOO?????????????");
    if (announcements) {
      console.log("HEEEEEEELLLLLLLOOOOO");
      confirm({
        type: "info",
        width: `60%`,
        icon: <PriorityFlag hasPriority={announcements[index].priority} />,
        okText: `Read`,
        onOk() {
          setIndex(index + 1);
          // markAnnouncementRead(announcement.identifier);
        },
        title: (
          <>
            <Text strong>{announcements[index].title}</Text>
            <br />
            <Text type="secondary" style={{ fontSize: `.8em` }}>
              {i18n(
                "ViewUnreadAnnouncement.create.details",
                announcements[index].user.username,
                formatDate({ date: announcements[index].createdDate })
              )}
            </Text>
          </>
        ),
        content: (
          <div style={{ overflowY: "auto", maxHeight: 600 }}>
            <Markdown source={announcements[index].message} />
          </div>
        ),
      });
    }
  }, [announcements]);

  return null;
}
