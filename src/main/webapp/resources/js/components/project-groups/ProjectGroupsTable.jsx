import React from "react";
import { PagedTable } from "../ant.design/PagedTable";
import { AddGroupButton } from "./AddGroupButton";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectRole } from "../project-members/ProjectRole";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { updateGroupsRoleOnProject } from "../../apis/projects/groups";

export function ProjectGroupsTable() {
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
        return <ProjectRole updateFn={updateGroupsRoleOnProject} user={item} />;
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
  //
  // if (window.PAGE.canManage) {
  //   alert("CAN MANAGE GROUPS");
  // }

  return <PagedTable buttons={<AddGroupButton />} columns={columns} />;
}
