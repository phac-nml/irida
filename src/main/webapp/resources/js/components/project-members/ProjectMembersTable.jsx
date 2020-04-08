import React, { useContext } from "react";
import { PagedTable, PagedTableContext } from "../ant.design/PagedTable";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectRoleSelect } from "./ProjectRoleSelect";
import { RemoveMemberButton } from "./RemoveMemberButton";
import { AddMembersButton } from "./AddMemberButton";

export function ProjectMembersTable() {
  const { updateTable } = useContext(PagedTableContext);

  const columns = [
    {
      title: i18n("ProjectMembersTable.name"),
      dataIndex: "name",
      render(text, item) {
        return <a href={setBaseUrl(`/users/${item.id}`)}>{text}</a>;
      },
    },
    {
      title: i18n("ProjectMembersTable.role"),
      dataIndex: "role",
      render(text, item) {
        return <ProjectRoleSelect user={item} />;
      },
    },
    {
      title: i18n("ProjectMembersTable.since"),
      dataIndex: "createdDate",
      render(text) {
        return formatInternationalizedDateTime(text);
      },
    },
  ];

  if (window.PAGE.canManage) {
    columns.push({
      align: "right",
      render(text, item) {
        return <RemoveMemberButton user={item} updateTable={updateTable} />;
      },
    });
  }

  return (
    <PagedTable
      buttons={[<AddMembersButton key="add-members-btn" />]}
      columns={columns}
    />
  );
}
