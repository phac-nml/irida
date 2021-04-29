import { Button } from "antd";
import React, { useContext } from "react";
import { useSelector } from "react-redux";
import {
  removeUserGroupFromProject,
  updateUserGroupRoleOnProject,
} from "../../apis/projects/user-groups";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";
import { PagedTable, PagedTableContext } from "../ant.design/PagedTable";
import { RemoveTableItemButton } from "../Buttons";
import { ProjectRole } from "../roles/ProjectRole";
import { AddGroupButton } from "./AddGroupButton";

/**
 * React component to render a table contain user groups associated with
 * the current project
 * @returns {string|*}
 * @constructor
 */
export function ProjectUserGroupsTable() {
  const { updateTable } = useContext(PagedTableContext);
  const { id: projectId, canManage } = useSelector((state) => state.project);

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
      title: i18n("ProjectUserGroupsTable.role"),
      render(text, group) {
        return (
          <ProjectRole
            item={group}
            updateRoleFn={updateUserGroupRoleOnProject}
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

  if (canManage) {
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
        canManage ? (
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
