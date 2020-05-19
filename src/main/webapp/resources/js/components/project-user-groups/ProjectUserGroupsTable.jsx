import React from "react";
import { Button, notification } from "antd";
import { PagedTable } from "../ant.design/PagedTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { RemoveTableItemButton } from "../Buttons";
import { removeUserGroupFromProject } from "../../apis/projects/user-groups";
import { AddGroupButton } from "./AddGroupButton";

export function ProjectUserGroupsTable() {
  function removeUserGroups(group) {
    removeUserGroupFromProject({ groupId: group.id }).then((message) => {
      notification.success({ message });
    });
  }

  const columns = [
    {
      dataIndex: "name",
      title: "GROUP NAME",
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
      title: "ROLE",
    },
    {
      dataIndex: "createdDate",
      title: "Date Added",
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
            confirmText={"SDFLKDSJLKSDJFKJSDLKF"}
            tooltipText={"KLDSJFLKDSJFLKSJDF"}
            onRemove={() => removeUserGroups(group)}
          />
        );
      },
    });
  }

  return (
    <PagedTable
      buttons={[<AddGroupButton key="add-group-btn" />]}
      search={true}
      columns={columns}
    />
  );
}
