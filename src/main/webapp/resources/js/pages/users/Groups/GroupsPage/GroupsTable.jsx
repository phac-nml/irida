import React from "react";
import { PagedTable } from "../../../../components/ant.design/PagedTable";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { RemoveItemButton } from "../../../../components/Buttons/RemoveItemButton";
import { deleteUserGroup } from "../../../../apis/users/groups";
import { Button } from "antd";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { Typography } from "antd";

const { Paragraph } = Typography;

/**
 * React component to render a table of paged user groups
 * @returns {string|null|*}
 * @constructor
 */
export default function GroupsTable() {
  const columns = [
    {
      title: i18n("UserGroups.name"),
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
      title: i18n("UserGroups.description"),
      dataIndex: "description",
      width: 355,
      render(text, item) {
        return (
          <Paragraph
            ellipsis={{ rows: 3, expandable: true }}
            style={{ width: 350, marginBottom: 0 }}
          >
            {text}
          </Paragraph>
        );
      },
    },
    {
      title: "Members",
      dataIndex: "members",
    },
    {
      title: i18n("UserGroups.created"),
      dataIndex: "createdDate",
      width: 200,
      render(text, item) {
        return formatInternationalizedDateTime(text);
      },
    },
    {
      title: i18n("UserGroups.modified"),
      dataIndex: "modifiedDate",
      width: 200,
      render(text, item) {
        return formatInternationalizedDateTime(text);
      },
    },
    {
      title: "",
      width: 80,
      dataIndex: "canManage",
      align: "center",
      fixed: "right",
      render(canManage, item) {
        if (canManage) {
          return (
            <RemoveItemButton
              removeFn={() => deleteUserGroup(item.id)}
              btnTooltip={i18n("UserGroups.remove.tooltip")}
              popoverLabel={i18n("UserGroups.remove.confirm")}
            />
          );
        }
        return null;
      },
    },
  ];
  return (
    <PagedTable
      columns={columns}
      search={true}
      buttons={
        <Button href={setBaseUrl("/groups/create")}>
          {i18n("UserGroups.create")}
        </Button>
      }
    />
  );
}
