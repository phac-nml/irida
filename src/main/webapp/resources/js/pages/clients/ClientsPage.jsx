import React from "react";
import { render } from "react-dom";
import { PagedTableProvider } from "../../contexts/PagedTableContext";
import { ClientsTable } from "./ClientsTable";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * React component to render the Remote API Clients page.
 * @returns {*}
 * @constructor
 */
export function ClientsPage({}) {
  return (
    <PagedTableProvider url={setBaseUrl("clients/ajax/list")}>
      <ClientsTable />
    </PagedTableProvider>
  );
}

render(<ClientsPage />, document.querySelector("#client-list-root"));
