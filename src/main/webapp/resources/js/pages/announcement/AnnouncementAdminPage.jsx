import React, { useRef } from "react";
import { render } from "react-dom";
import {
  createNewAnnouncement,
  deleteAnnouncement,
  updateAnnouncement
} from "../../apis/announcements/announcements";
import { CreateNewAnnouncement } from "./CreateNewAnnouncement";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import { PageWrapper } from "../../components/page/PageWrapper";
import { AnnouncementsTable } from "./AnnouncementsTable";

export function AnnouncementAdminPage({}) {
  const tableRef = useRef(null);

  function addNewAnnouncement(message) {
    createNewAnnouncement({ message }).then(() =>
      tableRef.current.updateTable()
    );
  }

  function updateTableAnnouncement({ id, message }) {
    updateAnnouncement({
      id,
      message
    }).then(() => tableRef.current.updateTable());
  }

  function deleteTableAnnouncement({ id }) {
    deleteAnnouncement({ id }).then(() => tableRef.current.updateTable());
  }

  return (
    <PageWrapper
      title={i18n("announcement.admin-menu")}
      headerExtras={
        <CreateNewAnnouncement createAnnouncement={addNewAnnouncement} />
      }
    >
      <PagedTableProvider url={setBaseUrl(`ajax/announcements/control/list`)}>
        <AnnouncementsTable
          ref={tableRef}
          updateAnnouncement={updateTableAnnouncement}
          deleteAnnouncement={deleteTableAnnouncement}
        />
      </PagedTableProvider>
    </PageWrapper>
  );
}

render(<AnnouncementAdminPage />, document.querySelector("#announcement-root"));
