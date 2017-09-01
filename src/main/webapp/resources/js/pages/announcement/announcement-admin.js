/**
 * Initializes the datatables on the announcements page.
 */
import $ from "jquery";
import {
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";
import "../../vendor/datatables/datatables";

// Generate the column names with order for this table.
const COLUMNS = generateColumnOrderInfo();

const $table = $("#announcementTable");

const config = Object.assign({}, tableConfig, {
  ajax: $table.data("url"),
  // Order the table by the announcement created date.
  order: [[COLUMNS.CREATED_DATE, "desc"]],
  columnDefs: [
    {
      targets: COLUMNS.MESSAGE,
      className: "preview-column",
      render(data, type, full) {
        // Message column is only a preview of the message.  This
        // needs to be rendered as a link to the full announcement.
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
        // Username needs to link to the users full profile.
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

/**
 * Move the buttons for the table into the appropriate location
 * above the table.
 */
const wrapper = $("#create-btn-wrapper");
$(".buttons").html(wrapper.html());
wrapper.remove();
