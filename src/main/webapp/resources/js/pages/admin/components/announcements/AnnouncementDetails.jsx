import React from "react";
import { Drawer } from "antd";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import AnnouncementUserTable from "./AnnouncementUserTable";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { PageWrapper } from "../../../../components/page/PageWrapper";

/**
 * Render React component to show the details of an announcement.
 * @param {object} announcement - the announcement that is to be displayed.
 * @returns {*}
 * @constructor
 */
export default function AnnouncementDetails({ announcement }) {
  const [visible, setVisible] = React.useState(false);

  return (
    <>
      <a onClick={() => setVisible(true)}>{announcement.title}</a>
      <Drawer
        title={i18n("announcement.control.details.title")}
        placement="right"
        closable={false}
        onClose={() => setVisible(false)}
        visible={visible}
        width={640}
      >
        <PageWrapper title="Announcement Read By Users">
          <PagedTableProvider
            url={setBaseUrl(
              `ajax/announcements/${announcement.id}/details/list`
            )}
          >
            <AnnouncementUserTable />
          </PagedTableProvider>
        </PageWrapper>
      </Drawer>
    </>
  );
}
