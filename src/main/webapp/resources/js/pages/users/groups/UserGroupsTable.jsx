import React from "react";
import { PagedTable } from "../../../components/ant.design/PagedTable";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { Button, Typography } from "antd";

const { Paragraph } = Typography;

export function UserGroupsTable() {
  const columns = [
    {
      title: i18n("UserGroupsTable.name"),
      dataIndex: "name",
      render(text, item) {
        return (
          <Button type="link" href={setBaseUrl(`/groups/${item.id}`)}>
            {text}
          </Button>
        );
      },
    },
    {
      dataIndex: i18n("UserGroupsTable.description"),
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
      width: 200,
      render(text) {
        return formatInternationalizedDateTime(text);
      },
    },
    {
      dataIndex: "canManage",
      render(canManage, item) {
        return canManage ? "delete" : null;
      },
    },
  ];

  return <PagedTable search={true} columns={columns} />;
}
