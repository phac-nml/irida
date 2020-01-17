import React, { useContext, useEffect, useState } from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../components/page/PageWrapper";
import {
  PagedTableContext,
  PagedTableProvider
} from "../../contexts/PagedTableContext";
import { Button, Input, Popconfirm, Table, Tag } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { dateColumnFormat } from "../../components/ant.design/table-renderers";
import { SPACE_XS } from "../../styles/spacing";
import { revokeClientTokens } from "../../apis/clients/clients";

function ClientsTable({}) {
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
        return record.tokens ? (
          <Popconfirm
            title={i18n("client.revoke.confirm", record.name)}
            placement={"topRight"}
            onConfirm={() => revokeTokens(record)}
          >
            <Button shape={"round"} size={"small"}>
              {i18n("client.details.token.revoke")}
            </Button>
          </Popconfirm>
        ) : null;
      }
    }
  ];

  function tableSearch(event) {
    onSearch(event.target.value);
  }

  /**
   * Revoke the tokens for the current client described
   * in the current row.
   */
  function revokeTokens(client) {
    revokeClientTokens(client.id).then(() => {
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

function ClientPage() {
  return (
    <PageWrapper title={i18n("clients.title")}>
      <PagedTableProvider url={setBaseUrl("clients/ajax/list")}>
        <ClientsTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}

render(<ClientPage />, document.querySelector("#client-root"));

// import $ from "jquery";
// import "../../vendor/datatables/datatables";
// import {
//   createItemLink,
//   generateColumnOrderInfo,
//   tableConfig
// } from "../../utilities/datatables-utilities";
// import { formatDate } from "../../utilities/date-utilities";
//
// const COLUMNS = generateColumnOrderInfo();
//
// const $table = $("#clientsTable");
// const config = Object.assign(tableConfig, {
//   ajax: $table.data("url"),
//   order: [[COLUMNS.CLIENT_ID, "desc"]],
//   columnDefs: [
//     {
//       className: "clientIdCol",
//       targets: COLUMNS.ID
//     },
//     {
//       render: function(data, type, row) {
//         return createItemLink({
//           label: data,
//           url: `${$table.data("clients")}${row.id}`
//         });
//       },
//       targets: COLUMNS.CLIENT_ID
//     },
//     {
//       targets: COLUMNS.CREATED_DATE,
//       render(data) {
//         const date = formatDate({ date: data });
//         return `<time>${date}</time>`;
//       }
//     }
//   ]
// });
//
// $table.DataTable(config);
//
// $(document).ready(() => {
//   const $addLink = $("#add-link");
//   $(".buttons").append($addLink);
//   $addLink.removeClass("hidden");
// });
