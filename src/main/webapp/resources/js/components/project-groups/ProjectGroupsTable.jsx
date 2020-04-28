import React from "react";
import { PagedTable } from "../ant.design/PagedTable";
import { AddGroupButton } from "./AddGroupButton";

export function ProjectGroupsTable() {
  const columns = [
    {
      title: "BAME",
      dataIndex: "name",
    },
  ];
  //
  // if (window.PAGE.canManage) {
  //   alert("CAN MANAGE GROUPS");
  // }

  return <PagedTable buttons={<AddGroupButton />} columns={columns} />;
}
