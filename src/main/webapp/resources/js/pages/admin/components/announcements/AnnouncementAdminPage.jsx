/**
 * @fileOverview Announcements administration page.
 */
import React, { useRef } from "react";
import {
  createNewAnnouncement,
  deleteAnnouncement,
  updateAnnouncement,
} from "../../../../apis/announcements/announcements";
import CreateNewAnnouncement from "./CreateNewAnnouncement";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { PageWrapper } from "../../../../components/page/PageWrapper";
import { AnnouncementsTable } from "./AnnouncementsTable";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import "../../../../../css/pages/announcements.css";

/**
 * React component to render the administration announcements page.
 * @returns {*}
 * @constructor
 */
export default function AnnouncementAdminPage() {
  const tableRef = useRef(null);

  function addNewAnnouncement(title, message, priority) {
    return createNewAnnouncement({ title, message, priority }).then(() =>
      tableRef.current.updateTable()
    );
  }

  function updateTableAnnouncement({ id, title, message, priority }) {
    return updateAnnouncement({
      id,
      title,
      message,
      priority,
    }).then(() => tableRef.current.updateTable());
  }

  function deleteTableAnnouncement({ id }) {
    deleteAnnouncement({ id }).then(() => tableRef.current.updateTable());
  }

  return (
    <PageWrapper
      title={i18n("AnnouncementAdminPage.title")}
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
