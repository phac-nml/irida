import "DataTables/datatables";
import { createItemLink, generateColumnOrderInfo, tableConfig } from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";

/*
Get the table headers and create a look up table for them.
This give the row name in snake case and its index.
 */
let COLUMNS = generateColumnOrderInfo();

const config = Object.assign(tableConfig, {
  ajax: window.PAGE.urls.table,
  order: [[COLUMNS.USERNAME, "desc"]],
  columnDefs: [
    {
      targets: [COLUMNS.USERNAME],
      render(data, type, full) {
        return createItemLink({
          url: `${window.PAGE.urls.link}${full.id}`,
          label: data
        });
      }
    },
    {
      targets: [COLUMNS.EMAIL],
      render(data) {
        return `<a href="mailto:${data}" class="btn btn-link">${data}</a>`
      }
    },
    {
      targets: [COLUMNS.CREATED_DATE, COLUMNS.MODIFIED_DATE],
      render(data) {
        const date = formatDate({ date: data });
        return `<time>${date}</time>`;
      }
    }
  ]
});

$("#usersTable").DataTable(config);

// (function ($, page) {
//     $(function () {
//         $('#usersTable').DataTable({
//             dom: "<'top'lf>rt<'bottom'ip><'clear'>",
//             processing: true,
//             serverSide: true,
//             deferRender: true,
//             ajax: page.urls.table,
//             stateSave: true,
//             stateDuration: -1,
//             order: [[1, "desc"]],
//             columnDefs: [
//                 {
//                     'render': function (data, type, row) {
//                         return '<a href="' + page.urls.link
//                             + row[0] + '">' + data + '</a>';
//                     },
//                     'targets': 1
//                 },
//                 {
//                     'render': function (data) {
//                         return '<span data-livestamp="' + data + '"></span>';
//                     },
//                     'targets': 7
//                 }
//             ]
//         });
//     });
// })(window.jQuery, window.PAGE);
