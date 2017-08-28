import $ from "jquery";
import { createItemLink, generateColumnOrderInfo, tableConfig } from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";
import "../../vendor/datatables/datatables";

const COLUMNS = generateColumnOrderInfo();
const $table = $("#announcementTable");

const config = Object.assign({}, tableConfig, {
  ajax: $table.data("url"),
  order: [[COLUMNS.CREATED_DATE, "desc"]],
  columnDefs: [
    {
      targets: COLUMNS.MESSAGE,
      className: "preview-column",
      render(data, type, full) {
        return createItemLink({
          url: `${window.PAGE.urls.link}${full.id}/details`,
          label: data,
          width: "100%"
        });
      }
    },
    {
      targets: COLUMNS.USER_USERNAME,
      render(data, type, full) {
        return createItemLink({
          url: `${window.PAGE.urls.user}${full.user.identifier}`,
          label: data
        });
      }
    },
    {
      targets: COLUMNS.CREATED_DATE,
      render(data) {
        const date = formatDate({ date: data });
        return `<time>${date}</time>`;
      }
    }
  ]
});

$table.DataTable(config);

const wrapper = $("#create-btn-wrapper");
$(".buttons").html(wrapper.html());
wrapper.remove();
