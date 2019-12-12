import $ from "jquery";
import {
  createButtonCell,
  createDeleteBtn,
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";
import "../../vendor/datatables/datatables";
import { showNotification } from "../../modules/notifications";

const table = $("#groupsTable");
const url = table.data("url");

const COLUMNS = generateColumnOrderInfo();

const config = Object.assign({}, tableConfig, {
  ajax: url,
  columnDefs: [
    {
      targets: [COLUMNS.NAME],
      render(data, type, full) {
        return createItemLink({
          url: `${window.PAGE.urls.link}${full.id}`,
          label: data
        });
      }
    },
    {
      targets: [COLUMNS.DESCRIPTION],
      render(data) {
        return `<p class="crop">${data}</p>`;
      }
    },
    {
      targets: [COLUMNS.CREATED_DATE, COLUMNS.MODIFIED_DATE],
      render(data) {
        return `<time>${formatDate({ date: data })}</time>`;
      }
    },
    {
      targets: -1,
      render(data, type, full) {
        if (full.admin || full.owner) {
          const btn = createDeleteBtn();
          btn.dataset.group = full.id;
          return createButtonCell([btn]);
        }
        return "";
      }
    }
  ]
});

const $dt = table.DataTable(config);

$dt.on("click", ".remove-btn", function(e) {
  const id = $(this).data("group");

  $("#removeGroupModal").load(
    window.PAGE.urls.deleteModal + "#removeGroupModalGen",
    { userGroupId: id },
    function() {
      const modal = $(this);
      modal.on("show.bs.modal", function() {
        $(this)
          .find("#remove-group-button")
          .off("click")
          .click(function() {
            $.ajax({
              url: `${window.PAGE.urls.deleteGroup}${id}`,
              type: "DELETE",
              success: function(result) {
                $dt.ajax.reload();
                showNotification({
                  text: result.result
                });
                modal.modal("hide");
              },
              error: function() {
                showNotification({
                  text: i18n("group.remove.notification.failure"),
                  type: "error"
                });
                modal.modal("hide");
              }
            });
          });
      });
      modal.modal("show");
    }
  );
});
