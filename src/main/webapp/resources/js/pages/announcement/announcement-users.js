/**
 * Initializes the DataTable on the announcements page to indicate who
 * has read the announcement.
 */
import $ from "jquery";
import { addTooltip } from "../../utilities/bootstrap-utilities";
import {
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";
import { createIcon, ICONS } from "../../utilities/fontawesome-utilities";
import "../../vendor/datatables/datatables";

// Generate the column names with order for this table.
const COLUMNS = generateColumnOrderInfo();
const $table = $("#announcementUsersTable");

const config = Object.assign({}, tableConfig, {
  ajax: $table.data("url"),
  // Order the table by the username
  order: [[COLUMNS.USERNAME, "asc"]],
  columnDefs: [
    {
      targets: COLUMNS.USERNAME,
      render(data, type, full) {
        // Render the username as a link to the users page.
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
          // If it is read, then add a check mark icon
          const icon = createIcon({ icon: ICONS.checkmark });
          icon.style.color = "green";
          // Add a tooltip incase the icon is not clear
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

// Initialize the datatable.
$table.DataTable(config);

/**
 * Update the positioning of the read counts to be
 * directly above the table.
 */
const readCounts = $("#read-counts").html();
$(".buttons").html(readCounts);
