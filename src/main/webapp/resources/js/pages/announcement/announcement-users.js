import $ from "jquery";
import { addTooltip } from "../../utilities/bootstrap-utilities";
import { createItemLink, generateColumnOrderInfo, tableConfig } from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";
import { createIcon, ICONS } from "../../utilities/fontawesome-utilities";
import "../../vendor/datatables/datatables";

const COLUMNS = generateColumnOrderInfo();
const $table = $("#announcementUsersTable");

const config = Object.assign({}, tableConfig, {
  ajax: $table.data("url"),
  order: [[COLUMNS.USERNAME, "asc"]],
  columnDefs: [
    {
      targets: COLUMNS.USERNAME,
      render(data, type, full) {
        return createItemLink({
          url: `${window.PAGE.urls.user}${full.user.identifier}`,
          label: full.user.username,
          width: "100%"
        });
      }
    },
    {
      targets: COLUMNS.STATUS,
      render(data, type, full) {
        if (full.dateRead) {
          const icon = createIcon({ icon: ICONS.checkmark });
          icon.style.color = "green";
          addTooltip({ dom: icon, title: "Read" });
          return icon.outerHTML;
        }
        return "";
      }
    },
    {
      targets: COLUMNS.DATE_READ,
      render(data) {
        if (data) {
          const date = formatDate({ date: data });
          return `<time>${date}</time>`;
        }
        return "";
      }
    }
  ]
});

$table.DataTable(config);

// Update the positioning of the read counts.
const readCounts = $("#read-counts").html();
$(".buttons").html(readCounts);
