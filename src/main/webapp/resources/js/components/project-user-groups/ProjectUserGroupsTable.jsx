import React, { useContext } from "react";
import { Button } from "antd";
import { PagedTable, PagedTableContext } from "../ant.design/PagedTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { RemoveTableItemButton } from "../Buttons";
import {
  removeUserGroupFromProject,
  updateUserGroupRoleOnProject,
} from "../../apis/projects/user-groups";
import { AddGroupButton } from "./AddGroupButton";
import { ProjectRole } from "../roles/ProjectRole";

/**
 * React component to render a table contain user groups associated with
 * the current project
 * @returns {string|*}
 * @constructor
 */
export function ProjectUserGroupsTable() {
  const { updateTable } = useContext(PagedTableContext);

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
          <ProjectRole item={group} updateFn={updateUserGroupRoleOnProject} />
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

  if (window.PAGE.canManage) {
    columns.push({
      align: "right",
      render(text, group) {
        return (
          <RemoveTableItemButton
            confirmText={i18n("usergroups.remove.confirm")}
            tooltipText={i18n("usergroups.remove.tooltip")}
            onRemove={() => removeUserGroupFromProject({groupId: group.id})}
            onRemoveSuccess={updateTable}
          />
        );
      },
    });
  }

  return (
    <PagedTable
      buttons={[
        <AddGroupButton
          key="add-group-btn"
          defaultRole="PROJECT_USER"
          onGroupAdded={updateTable}
        />,
      ]}
      search={true}
      columns={columns}
    />
  );
}
