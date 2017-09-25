import $ from "jquery";
import {
  createButtonCell,
  createDeleteBtn,
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../utilities/datatables-utilities";
import {formatDate} from "../../utilities/date-utilities";
import "../../vendor/datatables/datatables";

const table = $("#groupsTable");
const url = table.data("url");

const COLUMNS = generateColumnOrderInfo();

const config = Object.assign({}, tableConfig, {
  ajax: url,
  columnDefs: [
    {
      targets: [COLUMNS.GROUP_NAME],
      render(data, type, full) {
        return createItemLink({
          url: `${window.PAGE.urls.link}${full.group.identifier}`,
          label: data
        });
      }
    },
    {
      targets: [COLUMNS.GROUP_DESCRIPTION],
      render(data) {
        return `<p class="crop">${data}</p>`;
      }
    },
    {
      targets: [COLUMNS.GROUP_CREATED_DATE, COLUMNS.GROUP_MODIFIED_DATE],
      render(data) {
        return `<time>${formatDate({ date: data })}</time>`;
      }
    },
    {
      targets: -1,
      render() {
        return createButtonCell([createDeleteBtn()]);
      }
    }
  ],
  createdRow: function(row, data, index) {
    row.dataset.id = data.group.identifier;
    $(row).tooltip({ selector: "[data-toggle='tooltip']" });
  }
});

const $dt = table.DataTable(config);

$dt.on("click", ".remove-btn", function(e) {
  const $row = $(this).closest("tr");
  const id = $row.data("id");

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
                window.notifications.show({
                  msg: result.result
                });
                modal.modal("hide");
              },
              error: function() {
                window.notifications.show({
                  msg: window.PAGE.i18n.unexpectedRemoveError,
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
