import React from "react";
import { Button, Modal, Space } from "antd";
import AnnouncementForm from "./AnnouncementForm";
import { IconEdit } from "../../../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";
import {
  useVisibility,
  VisibilityProvider,
} from "../../../../contexts/visibility-context";

/**
 * Render a modal that displays the announcement form.
 * @param {object} announcement - the announcement that is to be updated.
 * @param {function} updateAnnouncement - the function that updates an announcement.
 * @returns {*}
 * @constructor
 */
function EditAnnouncementModal({ announcement, updateAnnouncement }) {
  const [visible, setVisibility] = useVisibility();

  return (
    <>
      <Button
        shape={"circle"}
        onClick={() => setVisibility(true)}
        className={"t-edit-announcement"}
      >
        <IconEdit />
      </Button>
      <Modal
        title={
          <Space>
            <IconEdit style={{ color: FONT_COLOR_PRIMARY }} />
            {i18n("EditAnnouncement.title")}
          </Space>
        }
        onCancel={() => setVisibility(false)}
        open={visible}
        width={640}
        footer={null}
        maskClosable={false}
      >
        <AnnouncementForm
          announcement={announcement}
          updateAnnouncement={updateAnnouncement}
        />
      </Modal>
    </>
  );
}

export default function EditAnnouncement({ announcement, updateAnnouncement }) {
  return (
    <VisibilityProvider>
      <EditAnnouncementModal
        announcement={announcement}
        updateAnnouncement={updateAnnouncement}
      />
    </VisibilityProvider>
  );
}
