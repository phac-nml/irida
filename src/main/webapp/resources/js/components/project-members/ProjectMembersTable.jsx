import React, { useContext } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  addMemberToProject,
  getAvailableUsersForProject,
  removeUserFromProject,
  updateUserMetadataRoleOnProject,
  updateUserRoleOnProject,
} from "../../apis/projects/members";
import { useGetProjectDetailsQuery } from "../../apis/projects/project";
import { useMetadataRoles } from "../../contexts/metadata-roles-context";
import { useProjectRoles } from "../../contexts/project-roles-context";
import { getCurrentUserDetails } from "../../pages/projects/redux/userSlice";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";
import { PagedTable, PagedTableContext } from "../ant.design/PagedTable";
import { AddMemberButton, RemoveTableItemButton } from "../Buttons";
import { RoleSelect } from "../roles/RoleSelect";
import { stringSorter } from "../../utilities/table-utilities";

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

  const { roles: projectRoles, getRoleFromKey } = useProjectRoles();
  const { roles: metadataRoles, getRoleFromKey: getMetadataRoleFromKey } =
    useMetadataRoles();

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

  const updateProjectRole =
    ({ id }) =>
    (projectRole) => {
      return updateUserRoleOnProject({ projectId, id, projectRole }).then(
        (message) => {
          updateTable();
          return message;
        }
      );
    };

  const updateMetadataRole =
    ({ id }) =>
    (metadataRole) => {
      return updateUserMetadataRoleOnProject({
        projectId,
        id,
        metadataRole,
      }).then((message) => {
        updateTable();
        return message;
      });
    };

  const columns = [
    {
      title: i18n("ProjectMembersTable.name"),
      dataIndex: "name",
      render(text, item) {
        return <a href={setBaseUrl(`/users/${item.id}`)}>{text}</a>;
      },
      sorter: stringSorter("name"),
      defaultSortOrder: "ascend",
      className: "t-user-name",
    },
    {
      title: i18n("ProjectMembersTable.projectRole"),
      dataIndex: "projectRole",
      render(text, item) {
        return project.canManageRemote ? (
          <RoleSelect
            className="t-project-role-select"
            roles={projectRoles}
            updateRoleFn={updateProjectRole(item)}
            currentRole={item.projectRole}
            disabledProjectOwner={item.id === userId}
          />
        ) : (
          getRoleFromKey(item.projectRole)
        );
      },
    },
    {
      title: i18n("ProjectMembersTable.metadataRole"),
      render(text, item) {
        return project.canManageRemote ? (
          <RoleSelect
            className="t-metadata-role-select"
            roles={metadataRoles}
            updateRoleFn={updateMetadataRole(item)}
            currentRole={item.metadataRole}
            disabledProjectOwner={
              item.id === userId || item.projectRole === "PROJECT_OWNER"
            }
          />
        ) : (
          getMetadataRoleFromKey(item.metadataRole)
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
            disabledLoggedInUser={userId === user.id}
          />
        );
      },
    });
  } else {
    // Remove the metadata role column if the user cannot manage the project
    const index = columns.findIndex(
      (key) => key.title === i18n("ProjectMembersTable.metadataRole")
    );
    columns.splice(index, 1);
  }

  /**
   * Add a new member to the current project
   *
   * @param {number} id - identifier for the user to add to the project
   * @param {string} role - project role for the user
   * @returns {Promise<AxiosResponse<*>>}
   */
  async function addMember({ id, projectRole, metadataRole }) {
    return addMemberToProject({ projectId, id, projectRole, metadataRole });
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
          />
        ) : null,
      ]}
      columns={columns}
    />
  );
}
