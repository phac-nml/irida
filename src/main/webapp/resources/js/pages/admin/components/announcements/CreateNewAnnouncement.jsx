import React from "react";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";
import { Drawer, Space } from "antd";
import AnnouncementForm from "./AnnouncementForm";
import { IconEdit } from "../../../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";

/**
 * Component to add a button which will open a drawer to create an announcement.
 * @param {function} createAnnouncement - the function that creates an announcement.
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
      <Drawer
        title={
          <Space>
            <IconEdit style={{ color: FONT_COLOR_PRIMARY }} />
            {i18n("CreateNewAnnouncement.title")}
          </Space>
        }
        placement="right"
        closable={false}
        onClose={() => setVisible(false)}
        visible={visible}
        width={640}
      >
        <AnnouncementForm createAnnouncement={createAnnouncement} />
      </Drawer>
    </>
  );
}
