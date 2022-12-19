import { Button, Table } from "antd";
import React from "react";
import {
  addMemberToUserGroup,
  getAvailableUsersForUserGroup,
  removeMemberFromUserGroup,
  updateUserRoleOnUserGroups,
} from "../../../apis/users/groups";
import { RemoveTableItemButton } from "../../../components/Buttons";
import { GroupRole } from "../../../components/roles/GroupRole";
import { SPACE_XS } from "../../../styles/spacing";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { stringSorter } from "../../../utilities/table-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { AddUserToGroupButton } from "../../admin/components/user-groups/AddUserToGroupButton";
import { getPaginationOptions } from "../../../utilities/antdesign-table-utilities";

/**
 * Custom sorter for the name column since this is NOT paged server side.
 * @type {function(*, *): number}
 */
const nameSorter = stringSorter("name");

/**
 * React component to render a table to display user group members
 * @param {array} members
 * @param {boolean} canManage can the current user manage members
 * @param {number} groupId identifier for teh current user group
 * @param {function} updateTable - method to refresh the contents of the table
 * @returns {string|*}
 * @constructor
 */
export default function UserGroupMembersTable({
  members,
  canManage,
  groupId,
  updateTable,
}) {
  const [total] = React.useState(members?.length);

  const columns = [
    {
      dataIndex: "name",
      title: i18n("UserGroupMembersTable.name"),
      sorter: nameSorter,
      render(text, user) {
        return (
          <Button type="link" href={setBaseUrl(`/users/${user.id}`)}>
            {text}
          </Button>
        );
      },
      defaultSortOrder: "ascend",
    },
    {
      title: i18n("UserGroupMembersTable.role"),
      dataIndex: "role",
      width: 200,
      render(text, user) {
        return (
          <GroupRole
            item={user}
            canManage={canManage}
            updateRoleFn={updateMemberRole}
          />
        );
      },
    },
    {
      title: i18n("UserGroupMembersTable.joined"),
      dataIndex: "createdDate",
      width: 230,
      render(text) {
        return formatInternationalizedDateTime(text);
      },
    },
  ];

  if (canManage) {
    columns.push({
      align: "right",
      width: 50,
      render(user) {
        return (
          <RemoveTableItemButton
            onRemove={() =>
              removeMemberFromUserGroup({ groupId, userId: user.id })
            }
            onRemoveSuccess={updateTable}
            tooltipText={i18n("UserGroupMembersTable.remove-tooltip")}
            confirmText={i18n("UserGroupMembersTable.remove-confirm")}
          />
        );
      },
    });
  }

  /*
   * Get the available users for a group based on a search parameter
   *
   * @param {string} query - search parameter for the user
   * @returns {Promise<AxiosResponse<*>>}
   */
  const getAvailableMembers = (query) =>
    getAvailableUsersForUserGroup({ id: groupId, query });

  /**
   * Add a member to this groups
   *
   * @param {number} id - identifier for the user to add
   * @param {string} role - group role to set the user to
   * @returns {Promise<AxiosResponse<*>>}
   */
  const addMember = ({ id, role }) => {
    return addMemberToUserGroup({ groupId, userId: id, role });
  };

  /**
   * Update a member role on this group
   *
   * @param {number} userId - identifier for the member to update
   * @param {string} role - group role to update the user to
   * @returns {Promise<AxiosResponse<*>>}
   */
  async function updateMemberRole({ userId, role }) {
    return updateUserRoleOnUserGroups({ groupId, userId, role });
  }

  return (
    <>
      <div style={{ display: "flex", marginBottom: SPACE_XS }}>
        <div style={{ flex: 1 }}>
          {canManage ? (
            <AddUserToGroupButton
              defaultRole="GROUP_MEMBER"
              label={i18n("UserGroupMembersTable.add")}
              modalTitle={i18n("UserGroupMembersTable.add.title")}
              addMemberFn={addMember}
              addMemberSuccessFn={updateTable}
              getAvailableMembersFn={getAvailableMembers}
            />
          ) : null}
        </div>
      </div>
      <Table
        pagination={getPaginationOptions(total)}
        columns={columns}
        dataSource={members}
      />
    </>
  );
}
