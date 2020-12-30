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
 * Component to add a button which will open a modal to view a read announcement.
 * @param {function} ViewReadAnnouncement - the function that displays a read announcement.
 * @returns {*}
 * @constructor
 */

const { Text } = Typography;

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
            {announcement.subject.title}
            {announcement.subject.priority && (
              <IconFlag style={{ color: FONT_COLOR_PRIMARY }} />
            )}
          </Space>
        }
        onCancel={() => setVisibility(false)}
        visible={visible}
        width={640}
        footer={null}
      >
        <Markdown source={announcement.subject.message} />
        <br />
        <Text type="secondary">
          Created by {announcement.subject.user.username} on{" "}
          {formatDate({ date: announcement.subject.createdDate })}
        </Text>
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
