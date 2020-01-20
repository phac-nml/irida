import React, { useContext, useEffect, useState } from "react";
import { PagedTableContext } from "../../../contexts/PagedTableContext";
import { Button, Input, Popconfirm, Table, Tag } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { dateColumnFormat } from "../../../components/ant.design/table-renderers";
import { SPACE_XS } from "../../../styles/spacing";
import { revokeClientTokens } from "../../../apis/clients/clients";

/**
 * Table for displaying a list of clients.
 * @return {*}
 * @constructor
 */
export function ClientsTable() {
  const {
    loading,
    total,
    pageSize,
    dataSource,
    onSearch,
    handleTableChange
  } = useContext(PagedTableContext);
  const [clients, setClients] = useState(dataSource);

  useEffect(() => setClients(dataSource), [dataSource]);

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
      width: 140,
      render(text, record) {
        const disabled = !record.tokens;
        return (
          <Popconfirm
            disabled={disabled}
            title={i18n("client.revoke.confirm", record.name)}
            placement={"topRight"}
            onConfirm={() => revokeTokens(record)}
          >
            <Button shape={"round"} size={"small"} disabled={disabled}>
              {i18n("client.details.token.revoke")}
            </Button>
          </Popconfirm>
        );
      }
    }
  ];

  /**
   * Handle searching through the external filter.
   * @param event
   */
  function tableSearch(event) {
    onSearch(event.target.value);
  }

  /**
   * Revoke the tokens for the current client described
   * in the current row.
   */
  function revokeTokens(client) {
    revokeClientTokens(client.id).then(() => {
      /*
      Once tokens have been revoked ensure that the table reflects this.
       */
      const c = [...clients];
      const index = c.findIndex(i => i.id === client.id);
      c[index].tokens = 0;
      setClients(c);
    });
  }

  return (
    <>
      <div
        style={{
          display: "flex",
          flexDirection: "row-reverse",
          marginBottom: SPACE_XS
        }}
      >
        <Input.Search style={{ width: 250 }} onChange={tableSearch} />
      </div>
      <Table
        columns={columns}
        keyKey={record => record.key}
        dataSource={clients}
        loading={loading}
        onChange={handleTableChange}
        pagination={{ total, pageSize, hideOnSinglePage: true }}
      />
    </>
  );
}

