import React from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { PagedTableProvider } from "../../../contexts/PagedTableContext";
import { ClientsTable } from "./ClientsTables";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { Button } from "antd";

/**
 * Page for displaying the list of all clients.
 * @return {*}
 * @constructor
 */
function ClientListingPage() {
  return (
    <PageWrapper
      title={i18n("clients.title")}
      headerExtras={
        <Button href={setBaseUrl(`clients/create`)}>
          {i18n("clients.add")}
        </Button>
      }
    >
      <PagedTableProvider url={setBaseUrl("clients/ajax/list")}>
        <ClientsTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}

render(<ClientListingPage />, document.querySelector("#client-root"));
