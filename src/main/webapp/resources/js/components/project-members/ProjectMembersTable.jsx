import React from "react";
import { PagedTable } from "../ant.design/PagedTable";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectRole } from "./ProjectRole";
import { RemoveMemberButton } from "./RemoveMemberButton";
import { AddMembersButton } from "./AddMemberButton";

/**
 * React component to display a table of project users.
 * @returns {string|*}
 * @constructor
 */
export function ProjectMembersTable() {
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
        return <ProjectRole user={item} />;
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
        return <RemoveMemberButton user={item} />;
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
