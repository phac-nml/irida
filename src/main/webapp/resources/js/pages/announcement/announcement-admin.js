import React, { useContext, useEffect, useState } from "react";
import { render } from "react-dom";
import { Table } from "antd";
import {
  PagedTableContext,
  PagedTableProvider
} from "../../contexts/PagedTableContext";
import { setBaseUrl } from "../../utilities/url-utilities";
import { PageWrapper } from "../../components/page/PageWrapper";
import { dateColumnFormat } from "../../components/ant.design/table-renderers";
import ReactMarkdown from "react-markdown";

function AnnouncementsTable() {
  const {
    loading,
    total,
    pageSize,
    dataSource,
    onSearch,
    handleTableChange
  } = useContext(PagedTableContext);

  const columns = [
    {
      title: i18n("iridaThing.id"),
      width: 80,
      dataIndex: "id",
      sorter: true
    },
    {
      title: i18n("announcement.control.message"),
      dataIndex: "message",
      render(text) {
        return (
          <ReactMarkdown
            source={text}
            disallowedTypes={["paragraph"]}
            unwrapDisallowed
          />
        );
      }
    },
    {
      title: i18n("announcement.control.createdBy"),
      dataIndex: "user",
      render(text, item) {
        return <a href={item.user.id}>{item.user.username}</a>;
      }
    },
    {
      ...dateColumnFormat(),
      title: i18n("iridaThing.timestamp"),
      dataIndex: "createdDate"
    }
  ];

  return (
    <Table
      dataSource={dataSource}
      columns={columns}
      loading={loading}
      onChange={handleTableChange}
      pagination={{ total, pageSize, hideOnSinglePage: true }}
    />
  );
}

render(
  <PageWrapper title={i18n("announcement.admin-menu")}>
    <PagedTableProvider url={setBaseUrl(`announcements/control/ajax/list`)}>
      <AnnouncementsTable />
    </PagedTableProvider>
  </PageWrapper>,
  document.querySelector("#announcement-root")
);

// /**
//  * Initializes the datatables on the announcements page.
//  */
// import $ from "jquery";
// import {
//   createItemLink,
//   generateColumnOrderInfo,
//   tableConfig
// } from "../../utilities/datatables-utilities";
// import { formatDate } from "../../utilities/date-utilities";
// import "../../vendor/datatables/datatables";
//
// // Generate the column names with order for this table.
// const COLUMNS = generateColumnOrderInfo();
//
// const $table = $("#announcementTable");
//
// const config = Object.assign({}, tableConfig, {
//   ajax: $table.data("url"),
//   // Order the table by the announcement created date.
//   order: [[COLUMNS.CREATED_DATE, "desc"]],
//   columnDefs: [
//     {
//       targets: COLUMNS.MESSAGE,
//       className: "preview-column",
//       render(data, type, full) {
//         // Message column is only a preview of the message.  This
//         // needs to be rendered as a link to the full announcement.
//         return createItemLink({
//           url: `${window.PAGE.urls.link}${full.id}/details`,
//           label: data,
//           width: "100%"
//         });
//       }
//     },
//     {
//       targets: COLUMNS.USER_USERNAME,
//       render(data, type, full) {
//         // Username needs to link to the users full profile.
//         return createItemLink({
//           url: `${window.PAGE.urls.user}${full.user.identifier}`,
//           label: data
//         });
//       }
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
// /**
//  * Move the buttons for the table into the appropriate location
//  * above the table.
//  */
// const wrapper = $("#create-btn-wrapper");
// $(".buttons").html(wrapper.html());
// wrapper.remove();
