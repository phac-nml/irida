import React from "react";
import { PagedTable } from "../../../components/ant.design/PagedTable";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { Typography } from "antd";
import { Link } from "@reach/router";
import { CreateNewUserGroupButton } from "./CreateNewUserGroupButton";

const { Paragraph } = Typography;

/**
 * React component for rendering a Table to display user groups.
 * @param baseUrl - either /admin/groups for admin panel or /groups for main app
 * baseUrl should already be set in parent component
 * @returns {*}
 * @constructor
 */
export function UserGroupsTable({ baseUrl }) {
  const columns = [
    {
      title: i18n("UserGroupsTable.name"),
      dataIndex: "name",
      sorter: true,
      render(text, item) {
        return <Link to={`${baseUrl}/${item.id}`}>{text}</Link>;
      },
    },
    {
      title: i18n("UserGroupsTable.description"),
      dataIndex: "description",
      sorter: true,
      width: 400,
      render(text) {
        return (
          <Paragraph
            style={{ marginBottom: 0 }}
            ellipsis={{ rows: 3, expandable: true }}
          >
            {text}
          </Paragraph>
        );
      },
    },
    {
      title: i18n("UserGroupsTable.created"),
      dataIndex: "createdDate",
      sorter: true,
      width: 220,
      render(text) {
        return formatInternationalizedDateTime(text);
      },
    },
  ];

  return (
    <PagedTable
      search={true}
      columns={columns}
      buttons={[<CreateNewUserGroupButton baseUrl={baseUrl} key="group-new" />]}
    />
  );
}
