import React from "react";
import { render } from "react-dom";
import { ClientsTable } from "./ClientsTable";

export function ClientsPage({}) {
  return (
    // <PagedTableProvider url="clients/ajax/list">
      <ClientsTable />
    // </PagedTableProvider>
  );
}

render(<ClientsPage />, document.querySelector("#client-list-root"));
