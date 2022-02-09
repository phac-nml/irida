import { Button, Popconfirm, Space, Tag, Typography } from "antd";
import React, { useContext } from "react";
import {
  deleteClient,
  revokeClientTokens,
} from "../../../../../apis/clients/clients";
import {
  PagedTable,
  PagedTableContext,
} from "../../../../../components/ant.design/PagedTable";
import {
  dateColumnFormat
} from "../../../../../components/ant.design/table-renderers";

/**
 * Table for displaying a list of clients.
 * @return {*}
 * @constructor
 */
export function ClientsTable() {
  const { updateTable } = useContext(PagedTableContext);

  const removeAndUpdate = async (id) => {
    await deleteClient(id);
    updateTable();
  };
  const columns = [
    {
      title: i18n("ClientsTable.column.id"),
      width: 80,
      dataIndex: ["details", "identifier"],
      sorter: true,
    },
    {
      title: i18n("ClientsTable.column.clientId"),
      dataIndex: ["details", "clientId"],
      ellipsis: true,
      sorter: true,
      render(name) {
        return <Typography.Text copyable>{name}</Typography.Text>;
      },
    },
    {
      ...dateColumnFormat(),
      title: i18n("ClientsTable.column.created"),
      dataIndex: ["details", "createdDate"],
    },
    {
      title: i18n("ClientsTable.column.grants"),
      dataIndex: ["details", "authorizedGrantTypes"],
      render(grants) {
        const colors = {
          password: "purple",
          authorization_code: "volcano",
          refresh_token: "magenta",
        };
        return (
          <div>
            {grants.map((g) => (
              <Tag color={colors[g] || ""} key={g}>
                {g}
              </Tag>
            ))}
          </div>
        );
      },
      width: 250,
    },
    {
      title: i18n("ClientsTable.column.scope"),
      dataIndex: ["details", "scope"],
      render(scopes) {
        console.log(scopes);
        const colors = {
          read: "cyan",
          write: "geekblue",
        };
        return (
          <div>
            {scopes.map((g) => (
              <Tag color={colors[g] || ""} key={g}>
                {g}
              </Tag>
            ))}
          </div>
        );
      },
      width: 150,
    },
    {
      title: i18n("ClientsTable.column.secret"),
      dataIndex: ["details", "clientSecret"],
      render(secret) {
        return secret ? (
          <Typography.Text copyable>{secret}</Typography.Text>
        ) : (
          ""
        );
      },
    },
    {
      title: i18n("ClientsTable.column.activeTokens"),
      dataIndex: "tokens",
      align: "right",
      width: 50,
    },
    {
      key: "action",
      align: "right",
      fixed: "right",
      width: 200,
      render(text, record) {
        const disabled = !record.tokens;
        return (
          <Space>
            <Popconfirm
              disabled={disabled}
              title={i18n("client.revoke.confirm", record.details.clientId)}
              placement={"topRight"}
              onConfirm={() => revokeTokens(record.details.identifier)}
            >
              <Button type="link" disabled={disabled} size="small">
                {i18n("client.details.token.revoke")}
              </Button>
            </Popconfirm>
            <Button type="link" size="small">
              Edit
            </Button>
            <Popconfirm
              title={i18n("ClientsTable.column.remove-confirm")}
              placement="topRight"
              onConfirm={() => removeAndUpdate(record.details.identifier)}
            >
              <Button type="link" size="small">
                {i18n("ClientsTable.column.remove")}
              </Button>
            </Popconfirm>
          </Space>
        );
      },
    },
  ];

  /**
   * Revoke the tokens for the current client described
   * in the current row.
   */
  function revokeTokens(id) {
    revokeClientTokens(id).then(updateTable);
  }

  return <PagedTable className={"t-admin-clients-table"} columns={columns} />;
}
