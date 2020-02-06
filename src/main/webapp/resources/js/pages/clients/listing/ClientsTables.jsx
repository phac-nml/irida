import React, { useContext } from "react";
import {
  PagedTable,
  PagedTableContext
} from "../../../components/ant.design/PagedTable";
import { Button, Popconfirm, Tag } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { dateColumnFormat } from "../../../components/ant.design/table-renderers";
import { revokeClientTokens } from "../../../apis/clients/clients";
import { StopOutlined } from "@ant-design/icons";

/**
 * Table for displaying a list of clients.
 * @return {*}
 * @constructor
 */
export function ClientsTable() {
  const { updateTable } = useContext(PagedTableContext);

  const columns = [
    {
      title: i18n("iridaThing.id"),
      width: 80,
      dataIndex: "id",
      sorter: true
    },
    {
      title: i18n("client.clientid"),
      dataIndex: "name",
      ellipsis: true,
      sorter: true,
      render(text, item) {
        return (
          <a
            className="t-client-name"
            target="_blank"
            rel="noreferrer noopener"
            href={setBaseUrl(`clients/${item.id}`)}
          >
            {text}
          </a>
        );
      }
    },
    {
      title: i18n("client.grant-types"),
      dataIndex: "grants",
      render(grants) {
        const colors = { password: "purple", authorization_code: "volcano" };
        return (
          <div>
            {grants.map(g => (
              <Tag color={colors[g] || ""} key={g}>
                {g}
              </Tag>
            ))}
          </div>
        );
      }
    },
    {
      ...dateColumnFormat(),
      title: i18n("iridaThing.timestamp"),
      dataIndex: "createdDate"
    },
    {
      title: i18n("client.details.token.active"),
      dataIndex: "tokens",
      align: "right"
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
            title={i18n("client.revoke.confirm", record.name)}
            placement={"topRight"}
            onConfirm={() => revokeTokens(record.id)}
          >
            <Button disabled={disabled}>
              <StopOutlined />
              {i18n("client.details.token.revoke")}
            </Button>
          </Popconfirm>
        );
      }
    }
  ];

  /**
   * Revoke the tokens for the current client described
   * in the current row.
   */
  function revokeTokens(id) {
    revokeClientTokens(id).then(updateTable);
  }

  return <PagedTable columns={columns} />;
}
