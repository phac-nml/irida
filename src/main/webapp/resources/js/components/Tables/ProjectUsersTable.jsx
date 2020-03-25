import React from "react";
import { PagedTable } from "../ant.design/PagedTable";

export function ProjectUsersTable() {
  const columns = [{ dataIndex: "label" }];

  return <PagedTable columns={columns} />;
}
