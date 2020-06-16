import React, { useContext } from "react";
import { PagedTable, PagedTableContext } from "../ant.design/PagedTable";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectRole } from "../roles/ProjectRole";
import { AddMemberButton, RemoveTableItemButton } from "../Buttons";
import {
  addMemberToProject,
  getAvailableUsersForProject,
  removeUserFromProject,
  updateUserRoleOnProject,
} from "../../apis/projects/members";

/**
 * React component to display a table of project users.
 * @returns {string|*}
 * @constructor
 */
export function ProjectMembersTable() {
  const { updateTable } = useContext(PagedTableContext);

  function userRemoved(user) {
    if (user.id === window.PAGE.user) {
      // If the user can remove themselves from the project, then when they
      // are removed redirect them to their project page since they cannot
      // use this project anymore.
      window.location.href = setBaseUrl(`/projects`);
    }
    updateTable();
  }

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
        return <ProjectRole item={item} updateFn={updateUserRoleOnProject} />;
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
      render(text, user) {
        return (
          <RemoveTableItemButton
            onRemove={() => removeUserFromProject(user.id)}
            onRemoveSuccess={() => userRemoved(user)}
            tooltipText={i18n("RemoveMemberButton.tooltip")}
            confirmText={i18n("RemoveMemberButton.confirm")}
          />
        );
      },
    });
  }

  return (
    <PagedTable
      buttons={[
        <AddMemberButton
          key="add-members-btn"
          label={i18n("AddMemberButton.label")}
          modalTitle={i18n("AddMemberButton.modal.title")}
          addMemberFn={addMemberToProject}
          addMemberSuccessFn={updateTable}
          getAvailableMembersFn={getAvailableUsersForProject}
          defaultRole="PROJECT_USER"
        />,
      ]}
      columns={columns}
    />
  );
}
