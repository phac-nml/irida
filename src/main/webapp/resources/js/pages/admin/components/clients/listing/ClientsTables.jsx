import { Button, Popconfirm, Space, Tag, Tooltip, Typography } from "antd";
import { ReloadOutlined } from "@ant-design/icons";
import React, { useContext } from "react";
import {
  deleteClient,
  regenerateClientSecret,
  revokeClientTokens,
  updateClientDetails,
} from "../../../../../apis/clients/clients";
import {
  PagedTable,
  PagedTableContext,
} from "../../../../../components/ant.design/PagedTable";
import { dateColumnFormat } from "../../../../../components/ant.design/table-renderers";
import { AddClientModal } from "../add/AddClientModal";

/**
 * Table for displaying a list of clients.
 * @return {*}
 * @constructor
 */
export function ClientsTable() {
  const { updateTable } = useContext(PagedTableContext);

  /**
   * Remove client.
   * @param {number} id - Remove a client from IRIDA and update the table.
   */
  const removeAndUpdateTable = async (id) => {
    await deleteClient(id);
    updateTable();
  };

  /**
   * Generate a new client secret for the client.
   * @param {number} id - The id of the client to regenerate the secret for.
   */
  async function regenerateSecret(id) {
    await regenerateClientSecret(id);
    updateTable();
  }

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
        return (
          <Typography.Text className="t-client-id" copyable>
            {name}
          </Typography.Text>
        );
      },
    },
    {
      title: i18n("ClientsTable.column.secret"),
      dataIndex: ["details", "clientSecret"],
      render(secret, client) {
        return secret ? (
          <Space size="small">
            <Typography.Text className="t-client-secret" copyable>
              {secret}
            </Typography.Text>
            <Tooltip placement="right" title={"Regenerate Secret"}>
              <Button
                shape="round"
                onClick={() => regenerateSecret(client.details.identifier)}
                size="small"
                icon={<ReloadOutlined />}
              />
            </Tooltip>
          </Space>
        ) : (
          ""
        );
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
            {grants.map((grant) => (
              <Tag color={colors[grant] || ""} key={grant}>
                {grant}
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
        const colors = {
          read: "cyan",
          write: "geekblue",
        };
        return (
          <div>
            {scopes.map((scope) => (
              <Tag color={colors[scope] || ""} key={scope}>
                {scope}
              </Tag>
            ))}
          </div>
        );
      },
      width: 150,
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
        return (
          <Space>
            <Popconfirm
              title={i18n("client.revoke.confirm", record.details.clientId)}
              placement={"topRight"}
              onConfirm={() => revokeTokens(record.details.identifier)}
            >
              <Button type="link" size="small">
                {i18n("client.details.token.revoke")}
              </Button>
            </Popconfirm>
            <AddClientModal
              existing={record.details}
              onComplete={updateClientDetails}
            >
              <Button type="link" size="small">
                {i18n("ClientsTable.edit")}
              </Button>
            </AddClientModal>
            <Popconfirm
              title={i18n("ClientsTable.column.remove-confirm")}
              placement="topRight"
              onConfirm={() => removeAndUpdateTable(record.details.identifier)}
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
