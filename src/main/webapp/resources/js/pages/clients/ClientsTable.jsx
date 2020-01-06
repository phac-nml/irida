import React, { useContext } from "react";
import { PagedTableContext } from "../../contexts/PagedTableContext";
import { PageWrapper } from "../../components/page/PageWrapper";
import { Button, Table } from "antd";

export function ClientsTable(props) {
  console.log(props);
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
      dataIndex: "id"
    }
  ];

  return (
    <PageWrapper
      title={i18n("clients.title")}
      headerExtras={<Button>{i18n("clients.add")}</Button>}
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
