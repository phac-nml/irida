import { Button } from "antd";
import React, { useContext } from "react";
import { useGetProjectDetailsQuery } from "../../apis/projects/project";
import {
  removeUserGroupFromProject,
  updateUserGroupProjectMetadataRole,
  updateUserGroupProjectRole,
} from "../../apis/projects/user-groups";
import { useMetadataRoles } from "../../contexts/metadata-roles-context";
import { useProjectRoles } from "../../contexts/project-roles-context";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
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
  const { roles: projectRoles, getRoleFromKey } = useProjectRoles(projectId);
  const { roles: metadataRoles, getRoleFromKey: getMetadataRoleFromKey } =
    useMetadataRoles();

  const updateProjectRole = (updatedRole, details) => (role) => {
    return updateUserGroupProjectRole({
      ...details,
      [updatedRole]: role,
    }).then((message) => {
      updateTable();
      return message;
    });
  };

  const updateProjectMetadataRole = (updatedRole, details) => (role) => {
    return updateUserGroupProjectMetadataRole({
      ...details,
      [updatedRole]: role,
    }).then((message) => {
      updateTable();
      return message;
    });
  };

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
        return project.canManageRemote ? (
          <RoleSelect
            updateRoleFn={updateProjectRole("projectRole", {
              id: group.id,
              projectRole: group.role,
              projectId,
            })}
            roles={projectRoles}
            currentRole={group.role}
          />
        ) : (
          getRoleFromKey(group.role)
        );
      },
    },
    {
      dataIndex: "metadataRole",
      title: i18n("ProjectUserGroupsTable.metadataData"),
      render(text, group) {
        return project.canManageRemote ? (
          <RoleSelect
            updateRoleFn={updateProjectMetadataRole("metadataRole", {
              id: group.id,
              metadataRole: group.metadataRole,
              projectId,
            })}
            roles={metadataRoles}
            currentRole={group.metadataRole}
            disabledProjectOwner={group.role === "PROJECT_OWNER"}
          />
        ) : (
          getMetadataRoleFromKey(group.metadataRole)
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
  } else {
    // Remove the metadata role column if the usergroup cannot manage the project
    const index = columns.findIndex(
      (key) => key.title === i18n("ProjectUserGroupsTable.metadataData")
    );
    columns.splice(index, 1);
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
