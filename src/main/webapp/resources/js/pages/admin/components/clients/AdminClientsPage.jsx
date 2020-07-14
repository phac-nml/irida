import React from "react";
import { PageWrapper } from "../../../../components/page/PageWrapper";
import {
  PagedTableProvider
} from "../../../../components/ant.design/PagedTable";
import { ClientsTable } from "./ClientsTable";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { AddClient } from "./AddClient";

/**
 * Page for displaying the list of all clients.
 * @return {*}
 * @constructor
 */
export default function AdminClientsPage() {
  return (
    <PageWrapper
      title={i18n("clients.title")}
      headerExtras={
        <AddClient />
      }
    >
      <PagedTableProvider url={setBaseUrl("/ajax/clients/list")}>
        <ClientsTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}
