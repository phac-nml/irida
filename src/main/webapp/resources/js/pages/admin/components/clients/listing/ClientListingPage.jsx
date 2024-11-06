import React from "react";
import { createClient } from "../../../../../apis/clients/clients";
import { PagedTableProvider } from "../../../../../components/ant.design/PagedTable";
import { AddNewButton } from "../../../../../components/Buttons/AddNewButton";
import { PageWrapper } from "../../../../../components/page/PageWrapper";
import { setBaseUrl } from "../../../../../utilities/url-utilities";
import { AddClientModal } from "../add/AddClientModal";
import { ClientsTable } from "./ClientsTables";

/**
 * Page for displaying the list of all clients.
 * @return {*}
 * @constructor
 */
export function ClientListingLayout() {
  const createNewClient = async (values) => {
    return await createClient(values);
  };

  return (
    <PageWrapper
      title={i18n("ClientListingPage.title")}
      headerExtras={[
        <AddClientModal onComplete={createNewClient}>
          <AddNewButton
            key="add-client"
            className={"t-add-client-btn"}
            text={i18n("AddClientButton.add")}
          />
        </AddClientModal>,
      ]}
    >
      <ClientsTable />
    </PageWrapper>
  );
}

export default function ClientListingPage() {
  return (
    <PagedTableProvider url={setBaseUrl("/ajax/clients/list")}>
      <ClientListingLayout />
    </PagedTableProvider>
  );
}
