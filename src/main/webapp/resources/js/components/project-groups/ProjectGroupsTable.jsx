import React from "react";
import { PagedTable } from "../ant.design/PagedTable";
import { AddGroupButton } from "./AddGroupButton";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectRole } from "../project-members/ProjectRole";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import {
  removeGroupFromProject,
  updateGroupsRoleOnProject,
} from "../../apis/projects/groups";
import { RemoveItemButton } from "../Buttons/RemoveItemButton";

export function ProjectGroupsTable() {
  const columns = [
    {
      title: i18n("ProjectGroupsTable.name"),
      dataIndex: "name",
      render(text, item) {
        return <a href={setBaseUrl(`/users/${item.id}`)}>{text}</a>;
      },
    },
    {
      title: i18n("ProjectGroupsTable.role"),
      dataIndex: "role",
      render(text, item) {
        return <ProjectRole updateFn={updateGroupsRoleOnProject} user={item} />;
      },
    },
    {
      title: i18n("ProjectGroupsTable.since"),
      dataIndex: "createdDate",
      render(text) {
        return formatInternationalizedDateTime(text);
      },
    },
  ];

  if (window.project.canManage) {
    columns.push({
      align: "right",
      render(text, item) {
        return (
          <RemoveItemButton
            removeFn={() => removeGroupFromProject(item.id)}
            popoverLabel={i18n("ProjectGroupsTable.remove.confirm")}
            btnTooltip={i18n("ProjectGroupsTable.remove.tooltip")}
          />
        );
      },
    });
  }

  return <PagedTable buttons={<AddGroupButton />} columns={columns} />;
}
