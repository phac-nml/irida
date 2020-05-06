import { UserGroupRole } from "../../../components/roles/UserGroupRole";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { Table } from "antd";
import React from "react";
import { SPACE_XS } from "../../../styles/spacing";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { stringSorter } from "../../../utilities/table-utilities";

const nameSorter = stringSorter("name");

export default function UserGroupMembersTable({ members, canManage, groupId }) {
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

  return (
    <>
      <div style={{ display: "flex", marginBottom: SPACE_XS }}>
        <div style={{ flex: 1 }}>
          <AddNewButton text={"Add new Member"} />
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
