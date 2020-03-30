import React from "react";
import { PagedTable } from "../ant.design/PagedTable";
import { nameColumnFormat } from "../ant.design/table-renderers";

export function ProjectUsersTable() {
  const columns = [
    {
      title: i18n("project.table.collaborator.name"),
      dataIndex: "name"
    }
  ];

  return <PagedTable columns={columns} />;
}
