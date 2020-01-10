import React, { useContext } from "react";
import { PagedTableContext } from "../../contexts/PagedTableContext";
import { PageWrapper } from "../../components/page/PageWrapper";
import { Button, Table, Tag } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { SPACE_XS } from "../../styles/spacing";
import { dateColumnFormat } from "../../components/ant.design/table-renderers";

export function ClientsTable() {
  const {
    loading,
    total,
    pageSize,
    dataSource,
    handleTableChange
  } = useContext(PagedTableContext);

  const columnDefs = [
    {
      title: i18n("iridaThing.id"),
      key: "id",
      dataIndex: "id",
      sorter: (a, b) => a - b
    },
    {
      title: i18n("client.clientid"),
      key: "name",
      dataIndex: "name",
      render(text, record) {
        return (
          <Button type="link" href={`clients/${record.id}`}>
            {text}
          </Button>
        );
      },
      sorter: (a, b) => ("" + a).localeCompare(b)
    },
    {
      title: i18n("client.grant-types"),
      key: "grants",
      dataIndex: "grants",
      render(text) {
        // Default colors for displaying grant types
        const colors = { password: "geekblue", authorization_code: "volcano" };
        // Comes back as a coma separated text list
        return text.split(",").map(grant => (
          <Tag
            key={grant}
            color={colors[grant] || ""}
            style={{ marginRight: SPACE_XS }}
          >
            {grant}
          </Tag>
        ));
      }
    },
    {
      ...dateColumnFormat(),
      title: i18n("iridaThing.timestamp"),
      key: "createdDate",
      dataIndex: "createdDate",
    },
    {
      title: i18n("client.details.token.active"),
      key: "activeTokens",
      dataIndex: "tokens",
      align: "right"
    }
  ];

  return (
    <PageWrapper
      title={i18n("clients.title")}
      headerExtras={
        <Button href={setBaseUrl("clients/create")}>
          {i18n("clients.add")}
        </Button>
      }
    >
      <Table
        columns={columnDefs}
        loading={loading}
        pagination={{ total, pageSize }}
        dataSource={dataSource}
        onChange={handleTableChange}
      />
    </PageWrapper>
  );
}
