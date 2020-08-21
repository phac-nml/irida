import React from "react";
import { PageWrapper } from "../../../../../components/page/PageWrapper";
import { PagedTableProvider } from "../../../../../components/ant.design/PagedTable";
import { ClientsTable } from "./ClientsTables";
import { setBaseUrl } from "../../../../../utilities/url-utilities";
import { AddNewButton } from "../../../../../components/Buttons/AddNewButton";

/**
 * Page for displaying the list of all clients.
 * @return {*}
 * @constructor
 */
export default function ClientListingPage() {
  return (
    <PageWrapper
      title={i18n("ClientListingPage.title")}
      headerExtras={
        <AddNewButton
          className={"t-add-client-btn"}
          href={setBaseUrl(`clients/create`)}
          text={i18n("clients.add")}
        />
      }
    >
      <PagedTableProvider url={setBaseUrl("/ajax/clients/list")}>
        <ClientsTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}
