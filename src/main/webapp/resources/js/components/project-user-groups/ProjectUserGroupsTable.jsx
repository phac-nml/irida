import { Button } from "antd";
import React, { useContext } from "react";
import { useGetProjectDetailsQuery } from "../../apis/projects/project";
import {
  removeUserGroupFromProject,
  updateUserGroupRoleOnProject,
} from "../../apis/projects/user-groups";
import { useProjectRoles } from "../../contexts/project-roles-context";
import {
  formatInternationalizedDateTime
} from "../../utilities/date-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";
import { PagedTable, PagedTableContext } from "../ant.design/PagedTable";
import { RemoveTableItemButton } from "../Buttons";
import { RoleSelect } from "../roles/RoleSelect";
import { AddGroupButton } from "./AddGroupButton";

/**
 * React component to render a table contain user groups associated with
 * the current project
 * @returns {string|*}
 * @constructor
 */
export function ProjectUserGroupsTable({ projectId }) {
  const { updateTable } = useContext(PagedTableContext);
  const { data: project = {} } = useGetProjectDetailsQuery(projectId);
  const { roles: projectRoles } = useProjectRoles(projectId);

  const columns = [
    {
      dataIndex: "name",
      title: i18n("ProjectUserGroupsTable.name"),
      render(text, group) {
        return (
          <Button type="link" href={setBaseUrl(`/groups/${group.id}`)}>
            {text}
          </Button>
        );
      },
    },
    {
      dataIndex: "role",
      title: i18n("ProjectUserGroupsTable.projectRole"),
      render(text, group) {
        return (
          <RoleSelect
            updateRoleFn={updateUserGroupRoleOnProject}
            roles={projectRoles}
            currentRole={group.role}
          />
        );
      },
    },
    {
      dataIndex: "createdDate",
      title: i18n("ProjectUserGroupsTable.created"),
      render(text) {
        return formatInternationalizedDateTime(text);
      },
    },
  ];

  if (project.canManageRemote) {
    columns.push({
      align: "right",
      render(text, group) {
        return (
          <RemoveTableItemButton
            confirmText={i18n("usergroups.remove.confirm")}
            tooltipText={i18n("usergroups.remove.tooltip")}
            onRemove={() =>
              removeUserGroupFromProject({ projectId, groupId: group.id })
            }
            onRemoveSuccess={updateTable}
          />
        );
      },
    });
  }

  return (
    <PagedTable
      buttons={[
        project.canManageRemote ? (
          <AddGroupButton
            key="add-group-btn"
            defaultRole="PROJECT_USER"
            onGroupAdded={updateTable}
            projectId={projectId}
          />
        ) : null,
      ]}
      search={true}
      columns={columns}
    />
  );
}
