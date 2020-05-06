import React from "react";
import { PagedTable } from "../../../components/ant.design/PagedTable";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { Button, Typography } from "antd";
import { RemoveTableItemButton } from "../../../components/Buttons";
import { deleteUserGroup } from "../../../apis/users/groups";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";

const { Paragraph } = Typography;

export function UserGroupsTable() {
  const columns = [
    {
      title: i18n("UserGroupsTable.name"),
      dataIndex: "name",
      sorter: true,
      render(text, item) {
        return (
          <Button type="link" href={setBaseUrl(`/groups/${item.id}`)}>
            {text}
          </Button>
        );
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
      width: 200,
      render(text) {
        return formatInternationalizedDateTime(text);
      },
    },
    {
      dataIndex: "canManage",
      align: "right",
      width: 50,
      render(canManage, group) {
        return canManage ? (
          <RemoveTableItemButton
            onRemove={() => deleteUserGroup(group.id)}
            confirmText={
              <div style={{ maxWidth: 250 }}>
                {i18n("UserGroupsTable.delete-confirm")}
              </div>
            }
            tooltipText={i18n("UserGroupsTable.delete-tooltip")}
          />
        ) : null;
      },
    },
  ];

  return (
    <PagedTable
      search={true}
      columns={columns}
      buttons={[
        <AddNewButton
          key="group-new"
          text={i18n("UserGroupsPage.create")}
          href={setBaseUrl("/groups/create")}
        />,
      ]}
    />
  );
}
