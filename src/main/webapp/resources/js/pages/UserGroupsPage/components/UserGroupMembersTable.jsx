import { UserGroupRole } from "../../../components/roles/UserGroupRole";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import React, { useContext } from "react";
import { SPACE_XS } from "../../../styles/spacing";
import { stringSorter } from "../../../utilities/table-utilities";
import { AddMemberButton } from "../../../components/Buttons/AddMemberButton";
import { UserGroupRolesContext } from "../../../contexts/UserGroupRolesContext";
import { RemoveTableItemButton } from "../../../components/Buttons";
import {
  addMemberToUserGroup,
  getAvailableUsersForUserGroup,
  removeMemberFromUserGroup,
} from "../../../apis/users/groups";
import { Button, Table } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";

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
  const { roles } = useContext(UserGroupRolesContext);

  /**
   * Remove a member from the group, and then refresh the table.
   * @param user
   * @returns {void | Promise<AxiosResponse<*>>}
   */
  function removeMember(user) {
    return removeMemberFromUserGroup({ groupId, userId: user.id }).then(
      (message) => {
        updateTable();
        return message;
      }
    );
  }

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
    },
    {
      title: i18n("UserGroupMembersTable.role"),
      dataIndex: "role",
      width: 200,
      render(text, user) {
        return (
          <UserGroupRole user={user} canManage={canManage} groupId={groupId} />
        );
      },
    },
    {
      title: i18n("UserGroupMembersTable.joined"),
      dataIndex: "createdDate",
      width: 200,
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
            onRemove={() => removeMember(user)}
            tooltipText={i18n("UserGroupMembersTable.remove-tooltip")}
            confirmText={i18n("UserGroupMembersTable.remove-confirm")}
          />
        );
      },
    });
  }

  const getAvailableMembers = (query) =>
    getAvailableUsersForUserGroup({ id: groupId, query });

  const addMember = ({ id, role }) => {
    return addMemberToUserGroup({ groupId, userId: id, role });
  };

  return (
    <>
      <div style={{ display: "flex", marginBottom: SPACE_XS }}>
        <div style={{ flex: 1 }}>
          {canManage ? (
            <AddMemberButton
              defaultRole="GROUP_MEMBER"
              roles={roles}
              addMemberFn={addMember}
              addMemberSuccessFn={updateTable}
              getAvailableMembersFn={getAvailableMembers}
            />
          ) : null}
        </div>
      </div>
      <Table
        pagination={{ hideOnSinglePage: true }}
        columns={columns}
        dataSource={members}
      />
    </>
  );
}
