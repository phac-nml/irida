import $ from "jquery";
import "../../vendor/datatables/datatables";
import {
  tableConfig,
  generateColumnOrderInfo,
  createItemLink,
  createButtonCell,
  createDeleteBtn
} from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";

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

// var groupsTable = (function(page) {
// 	function groupLinkRow(data, type, full) {
// 		return '<a class="item-link" title="' + data + '" href="' + page.urls.link
// 				+ full.group.identifier + '"><span>' + data + '</span></a>';
// 	};
//
// 	function descriptionRow(data, type, full) {
// 		return '<p class="crop">' + full.group.description + '</p>';
// 	};
//
// 	function removeGroupButton(data, type, full) {
// 		if (full.groupOwner || full.admin) {
// 			return "<div class='btn-group pull-right' data-toggle='tooltip' data-placement='left' title='" + page.i18n.remove + "'><button type='button' class='btn btn-default btn-xs remove-group-btn'><span class='fa fa-remove'></span></div>";
// 		} else {
// 			return "";
// 		}
// 	};
//
// 	function deleteLinkCallback(row, data) {
// 		var row = $(row);
// 		row.find(".remove-group-btn").click(function () {
// $("#removeGroupModal").load(
//   page.urls.deleteModal + "#removeGroupModalGen",
//   { userGroupId: data.group.identifier },
//   function() {
//     var modal = $(this);
//     modal.on("show.bs.modal", function() {
//       $(this)
//         .find("#remove-group-button")
//         .off("click")
//         .click(function() {
//           $.ajax({
//             url: page.urls.deleteGroup + data.group.identifier,
//             type: "DELETE",
//             success: function(result) {
//               oTable_groupsTable.ajax.reload();
//               notifications.show({
//                 msg: result.result
//               });
//               modal.modal("hide");
//             },
//             error: function() {
//               notifications.show({
//                 msg: page.i18n.unexpectedRemoveError,
//                 type: "error"
//               });
//               modal.modal("hide");
//             }
//           });
//         });
//     });
//     modal.modal("show");
//   }
// );
// });
// 		row.find('[data-toggle="tooltip"]').tooltip();
// 	};
//
// 	return {
// 		groupLinkRow : groupLinkRow,
// 		removeGroupButton : removeGroupButton,
// 		deleteLinkCallback : deleteLinkCallback,
// 		descriptionRow : descriptionRow
// 	};
// })(window.PAGE);
