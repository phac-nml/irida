import React, { useContext } from "react";
import { PagedTable, PagedTableContext } from "../ant.design/PagedTable";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectRole } from "../roles/ProjectRole";
import { RemoveTableItemButton } from "../Buttons";
import { AddMembersButton } from "./AddMemberButton";
import { removeUserFromProject } from "../../apis/projects/members";

/**
 * React component to display a table of project users.
 * @returns {string|*}
 * @constructor
 */
export function ProjectMembersTable() {
  const { updateTable } = useContext(PagedTableContext);

  function removeUser(user) {
    return removeUserFromProject(user.id).then((message) => {
      if (user.id === window.PAGE.user) {
        // If the user can remove themselves from the project, then when they
        // are removed redirect them to their project page since they cannot
        // use this project anymore.
        window.location.href = setBaseUrl(`/projects`);
      }
      updateTable();
      return message;
    });
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
      render(text, user) {
        return (
          <RemoveTableItemButton
            onRemove={() => removeUser(user)}
            tooltipText={i18n("RemoveMemberButton.tooltip")}
            confirmText={i18n("RemoveMemberButton.confirm")}
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
