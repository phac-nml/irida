import React from "react";
import { Button, Modal, Space } from "antd";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import AnnouncementUserTable from "./AnnouncementUserTable";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { IconEye } from "../../../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";
import {
  useVisibility,
  VisibilityProvider,
} from "../../../../contexts/visibility-context";

/**
 * Render a modal that displays a table of the users who read the announcement.
 * @param {object} announcement - the announcement that is to be displayed.
 * @returns {*}
 * @constructor
 */
function ViewAnnouncementModal({ announcement }) {
  const [visible, setVisibility] = useVisibility();

  return (
    <>
      <Button
        shape={"circle"}
        onClick={() => setVisibility(true)}
        className={"t-view-announcement"}
      >
        <IconEye />
      </Button>
      <Modal
        title={
          <Space>
            <IconEye style={{ color: FONT_COLOR_PRIMARY }} />
            {i18n("ViewAnnouncement.title")}
          </Space>
        }
        onCancel={() => setVisibility(false)}
        open={visible}
        width={640}
        footer={null}
        maskClosable={false}
      >
        <PagedTableProvider
          url={setBaseUrl(`ajax/announcements/${announcement.id}/details/list`)}
        >
          <AnnouncementUserTable />
        </PagedTableProvider>
      </Modal>
    </>
  );
}

export default function ViewAnnouncement({ announcement }) {
  return (
    <VisibilityProvider>
      <ViewAnnouncementModal announcement={announcement} />
    </VisibilityProvider>
  );
}
