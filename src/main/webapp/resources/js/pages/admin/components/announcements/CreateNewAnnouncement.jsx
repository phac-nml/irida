import React from "react";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";
import { CreateAnnouncementModal } from "./CreateAnnouncementModal";

/**
 * Component to add a button which will open a modal to create an announcement.
 * @param {function} createAnnouncement
 * @returns {*}
 * @constructor
 */
export function CreateNewAnnouncement({ createAnnouncement }) {
  const [visible, setVisible] = React.useState(false);

  return (
    <>
      <AddNewButton
        className="t-create-announcement"
        onClick={() => setVisible(true)}
        text={i18n("CreateNewAnnouncement.title")}
      />
      <CreateAnnouncementModal
        visible={visible}
        closeModal={() => setVisible(false)}
        createAnnouncement={createAnnouncement}
      />
    </>
  );
}
