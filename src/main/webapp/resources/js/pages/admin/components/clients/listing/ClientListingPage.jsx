import React from "react";
import { PageWrapper } from "../../../../../components/page/PageWrapper";
import { PagedTableProvider } from "../../../../../components/ant.design/PagedTable";
import { ClientsTable } from "./ClientsTables";
import { setBaseUrl } from "../../../../../utilities/url-utilities";
import { AddClientButton } from "../add/AddClientButton";

/**
 * Page for displaying the list of all clients.
 * @return {*}
 * @constructor
 */
export default function ClientListingPage() {
  return (
    <PagedTableProvider url={setBaseUrl("/ajax/clients/list")}>
      <PageWrapper
        title={i18n("ClientListingPage.title")}
        headerExtras={<AddClientButton />}
      >
        <ClientsTable />
      </PageWrapper>
    </PagedTableProvider>
  );
}
