import { UserGroupRole } from "../../../components/roles/UserGroupRole";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { Table } from "antd";
import React, { useContext } from "react";
import { SPACE_XS } from "../../../styles/spacing";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { stringSorter } from "../../../utilities/table-utilities";
import { AddUserGroupMember } from "./AddUserGroupMember";
import { AddMemberButton } from "../../../components/Buttons/AddMemberButton";
import {
  addMemberToUserGroup,
  getAvailableUsersForUserGroup,
} from "../../../apis/users/groups";
import { UserGroupRolesContext } from "../../../contexts/UserGroupRolesContext";

const nameSorter = stringSorter("name");

export default function UserGroupMembersTable({
  members,
  canManage,
  groupId,
  updateTable,
}) {
  const { roles } = useContext(UserGroupRolesContext);

  const columns = [
    {
      dataIndex: "name",
      title: "Member Name",
      sorter: nameSorter,
    },
    {
      title: "role",
      dataIndex: "role",
      width: 200,
      render(text, user) {
        return (
          <UserGroupRole user={user} canManage={canManage} groupId={groupId} />
        );
      },
    },
    {
      title: "Joined",
      dataIndex: "createdDate",
      width: 200,
      render(text) {
        return formatInternationalizedDateTime(text);
      },
    },
  ];

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
