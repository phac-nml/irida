/**
 * @fileOverview Announcements administration page.
 */
import React, { useRef } from "react";
import { render } from "react-dom";
import {
  createNewAnnouncement,
  deleteAnnouncement,
  updateAnnouncement
} from "../../../../apis/announcements/announcements";
import { CreateNewAnnouncement } from "./CreateNewAnnouncement";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { PageWrapper } from "../../../../components/page/PageWrapper";
import { AnnouncementsTable } from "./AnnouncementsTable";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";

/**
 * React component to render the administration announcements page.
 * @returns {*}
 * @constructor
 */
export default function AnnouncementAdminPage({}) {
  const tableRef = useRef(null);

  function addNewAnnouncement(title, message, priority) {
    createNewAnnouncement({ title, message, priority }).then(() =>
      tableRef.current.updateTable()
    );
  }

  function updateTableAnnouncement({ id, title, message, priority }) {
    updateAnnouncement({
      id,
      title,
      message,
      priority
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