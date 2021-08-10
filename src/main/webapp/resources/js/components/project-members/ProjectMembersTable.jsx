import React, { useContext } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  addMemberToProject,
  getAvailableUsersForProject,
  removeUserFromProject,
  updateUserRoleOnProject,
} from "../../apis/projects/members";
import { useGetProjectDetailsQuery } from "../../apis/projects/project";
import { getCurrentUserDetails } from "../../pages/projects/redux/userSlice";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";
import { PagedTable, PagedTableContext } from "../ant.design/PagedTable";
import { AddMemberButton, RemoveTableItemButton } from "../Buttons";
import { ProjectRole } from "../roles/ProjectRole";

/**
 * React component to display a table of project users.
 * @returns {string|*}
 * @constructor
 */
export function ProjectMembersTable({ projectId }) {
  const dispatch = useDispatch();
  const { updateTable } = useContext(PagedTableContext);
  const { data: project = {} } = useGetProjectDetailsQuery(projectId);
  const { identifier: userId } = useSelector((state) => state.user);

  React.useEffect(() => {
    dispatch(getCurrentUserDetails());
  }, [dispatch]);

  function userRemoved(user) {
    if (user.id === userId) {
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
        return (
          <ProjectRole
            projectId={projectId}
            item={item}
            updateRoleFn={updateUserRoleOnProject}
          />
        );
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

  if (project.canManageRemote) {
    columns.push({
      align: "right",
      render(text, user) {
        return (
          <RemoveTableItemButton
            onRemove={() => removeUserFromProject({ projectId, id: user.id })}
            onRemoveSuccess={() => userRemoved(user)}
            tooltipText={i18n("RemoveMemberButton.tooltip")}
            confirmText={i18n("RemoveMemberButton.confirm")}
          />
        );
      },
    });
  }

  /**
   * Add a new member to the current project
   *
   * @param {number} id - identifier for the user to add to the project
   * @param {string} role - project role for the user
   * @returns {Promise<AxiosResponse<*>>}
   */
  async function addMember({ id, role }) {
    return addMemberToProject({ projectId, id, role });
  }

  /**
   * Get available users for the current project based on a search query
   *
   * @param {string} query - term to search for user by
   * @returns {Promise<AxiosResponse<*>>}
   */
  async function getAvailableUsers(query) {
    return getAvailableUsersForProject({ projectId, query });
  }

  return (
    <PagedTable
      buttons={[
        project.canManageRemote ? (
          <AddMemberButton
            key="add-members-btn"
            label={i18n("AddMemberButton.label")}
            modalTitle={i18n("AddMemberButton.modal.title")}
            addMemberFn={addMember}
            addMemberSuccessFn={updateTable}
            getAvailableMembersFn={getAvailableUsers}
            defaultRole="PROJECT_USER"
          />
        ) : null,
      ]}
      columns={columns}
    />
  );
}
