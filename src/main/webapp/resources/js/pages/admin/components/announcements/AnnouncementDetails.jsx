import React from "react";
import { Drawer, Space, Tabs } from "antd";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import AnnouncementUserTable from "./AnnouncementUserTable";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import AnnouncementForm from "./AnnouncementForm";
import { IconEdit } from "../../../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";

/**
 * Render React component to show the details of an announcement.
 * @param {object} announcement - the announcement that is to be displayed.
 * @param {function} updateAnnouncement - the function that updates an announcement.
 * @param {function} deleteAnnouncement - the function that deletes an announcement.
 * @returns {*}
 * @constructor
 */
export default function AnnouncementDetails({
  announcement,
  updateAnnouncement,
  deleteAnnouncement,
}) {
  const [visible, setVisible] = React.useState(false);
  const { TabPane } = Tabs;

  return (
    <>
      <a onClick={() => setVisible(true)}>{announcement.title}</a>
      <Drawer
        title={
          <Space>
            <IconEdit style={{ color: FONT_COLOR_PRIMARY }} />
            {i18n("AnnouncementDetails.title")}
          </Space>
        }
        placement="right"
        closable={false}
        onClose={() => setVisible(false)}
        visible={visible}
        width={640}
      >
        <Tabs defaultActiveKey="1">
          <TabPane tab="Edit" key="1">
            <AnnouncementForm
              announcement={announcement}
              updateAnnouncement={updateAnnouncement}
              deleteAnnouncement={deleteAnnouncement}
            />
          </TabPane>
          <TabPane tab="Views" key="2">
            <PagedTableProvider
              url={setBaseUrl(
                `ajax/announcements/${announcement.id}/details/list`
              )}
            >
              <AnnouncementUserTable />
            </PagedTableProvider>
          </TabPane>
        </Tabs>
      </Drawer>
    </>
  );
}
