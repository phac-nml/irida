import React from "react";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";
import { Drawer, Space } from "antd";
import AnnouncementForm from "./AnnouncementForm";
import { IconEdit } from "../../../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";
import {
  useVisibility,
  VisibilityProvider,
} from "../../../../contexts/visibility-context";

/**
 * Component to add a button which will open a drawer to create an announcement.
 * @param {function} createAnnouncement - the function that creates an announcement.
 * @returns {*}
 * @constructor
 */
function CreateNewAnnouncementButton({ createAnnouncement }) {
  const [visible, setVisibility] = useVisibility();

  return (
    <>
      <AddNewButton
        className="t-create-announcement"
        onClick={() => setVisibility(true)}
        text={i18n("CreateNewAnnouncement.title")}
      />
      <Drawer
        title={
          <Space>
            <IconEdit style={{ color: FONT_COLOR_PRIMARY }} />
            {i18n("CreateNewAnnouncement.title")}
          </Space>
        }
        placement="right"
        onClose={() => setVisibility(false)}
        visible={visible}
        width={640}
      >
        <AnnouncementForm createAnnouncement={createAnnouncement} />
      </Drawer>
    </>
  );
}

export default function CreateNewAnnouncement({ createAnnouncement }) {
  return (
    <VisibilityProvider>
      <CreateNewAnnouncementButton createAnnouncement={createAnnouncement} />
    </VisibilityProvider>
  );
}
