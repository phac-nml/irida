import React from "react";
import { render } from "react-dom";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { ClientsTable } from "./ClientsTable";
import { setBaseUrl } from "../../../../utilities/url-utilities";

/**
 * React component to render the Remote API Clients page.
 * @returns {*}
 * @constructor
 */
export default function ClientsPage({}) {
  return (
    <PagedTableProvider url={setBaseUrl("/ajax/clients/list")}>
      <ClientsTable />
    </PagedTableProvider>
  );
}
