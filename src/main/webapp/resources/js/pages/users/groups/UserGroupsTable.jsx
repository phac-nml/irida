import React from "react";
import { PagedTable } from "../../../components/ant.design/PagedTable";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { Button, Typography } from "antd";

const { Paragraph } = Typography;

export function UserGroupsTable() {
  const columns = [
    {
      title: "Name",
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
      dataIndex: "description",
      width: 400,
      render(text, item) {
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
      dataIndex: "createdDate",
      width: 200,
      render(text, item) {
        return formatInternationalizedDateTime(text);
      },
    },
    {
      dataIndex: "modifiedDate",
      width: 200,
      render(text, item) {
        return formatInternationalizedDateTime(text);
      },
    },
  ];

  return <PagedTable search={true} columns={columns} />;
}
