import { Button, Popconfirm, Tag } from "antd";
import React, { useContext } from "react";
import { revokeClientTokens } from "../../../../../apis/clients/clients";
import {
  PagedTable,
  PagedTableContext,
} from "../../../../../components/ant.design/PagedTable";
import {
  dateColumnFormat
} from "../../../../../components/ant.design/table-renderers";
import { IconStop } from "../../../../../components/icons/Icons";

/**
 * Table for displaying a list of clients.
 * @return {*}
 * @constructor
 */
export function ClientsTable() {
  const { updateTable } = useContext(PagedTableContext);

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
    },
    {
      title: i18n("ClientsTable.column.grants"),
      dataIndex: ["details", "authorizedGrantTypes"],
      render(grants) {
        console.log(grants);
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
    },
    {
      ...dateColumnFormat(),
      title: i18n("ClientsTable.column.created"),
      dataIndex: ["details", "createdDate"],
    },
    {
      title: i18n("ClientsTable.column.activeTokens"),
      dataIndex: "tokens",
      align: "right",
    },
    {
      key: "action",
      align: "right",
      fixed: "right",
      width: 200,
      render(text, record) {
        const disabled = !record.tokens;
        return (
          <Popconfirm
            disabled={disabled}
            title={i18n("client.revoke.confirm", record.details.clientId)}
            placement={"topRight"}
            onConfirm={() => revokeTokens(record.details.identifier)}
          >
            <Button disabled={disabled}>
              <IconStop />
              {i18n("client.details.token.revoke")}
            </Button>
          </Popconfirm>
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
