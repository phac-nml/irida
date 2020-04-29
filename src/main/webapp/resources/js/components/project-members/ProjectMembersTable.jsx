import React from "react";
import { PagedTable } from "../ant.design/PagedTable";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectRole } from "./ProjectRole";
import { RemoveItemButton } from "../Buttons/RemoveItemButton";
import { AddMembersButton } from "./AddMemberButton";
import {
  removeUserFromProject,
  updateUserRoleOnProject,
} from "../../apis/projects/members";

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
        return <ProjectRole updateFn={updateUserRoleOnProject} user={item} />;
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

  function onRemoveMemberSuccess(user) {
    if (user.id === window.PAGE.user) {
      // If the user can remove themselves from the project, then when they
      // are removed redirect them to their project page since they cannot
      // use this project anymore.
      window.location.href = setBaseUrl(`/projects`);
    }
  }

  if (window.project.canManage) {
    columns.push({
      align: "right",
      render(text, item) {
        return (
          <RemoveItemButton
            removeFn={() => removeUserFromProject(item.id)}
            onRemoveSuccess={() => onRemoveMemberSuccess(item)}
            btnTooltip={i18n("ProjectMembersTable.remove.tooltip")}
            popoverLabel={i18n("ProjectMembersTable.remove.confirm")}
          />
        );
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
