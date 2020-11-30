import React from "react";
import { Button } from "antd";
import { IconEdit } from "../../../../components/icons/Icons";
import { AnnouncementModal } from "./AnnouncementModal";

/**
 * Render React component to edit an announcement.
 * @param {object} announcement - the announcement that is to be updated.
 * @param {function} updateAnnouncement - function to update the announcement.
 * @returns {*}
 * @constructor
 */
export function EditAnnouncement({ announcement, updateAnnouncement }) {
  const [visible, setVisible] = React.useState(false);

  return (
    <>
      <Button shape={"circle"} onClick={() => setVisible(true)}>
        <IconEdit />
      </Button>
      <AnnouncementModal
        visible={visible}
        closeModal={() => setVisible(false)}
        announcement={announcement}
        updateAnnouncement={updateAnnouncement}
      />
    </>
  );
}
