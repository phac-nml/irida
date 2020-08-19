import React from "react";
import { PageWrapper } from "../../../../components/page/PageWrapper";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { ClientsTable } from "./ClientsTable";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { AddClientButton } from "./AddClientButton";

/**
 * Page for displaying the list of all clients.
 * @return {*}
 * @constructor
 */
export default function ClientListingPage() {
  return (
    <PageWrapper
      title={i18n("clients.title")}
      headerExtras={<AddClientButton />}
    >
      <PagedTableProvider url={setBaseUrl("/ajax/clients/list")}>
        <ClientsTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}
