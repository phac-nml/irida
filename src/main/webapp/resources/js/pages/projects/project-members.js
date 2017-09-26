import $ from "jquery";
import "./../../vendor/plugins/jquery/select2";
import "./../../vendor/datatables/datatables";
import {
  tableConfig,
  createItemLink,
  generateColumnOrderInfo,
  createDeleteBtn,
  createButtonCell
} from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";

const $table = $("#usersTable");
const roleTemplateWrapper = document.querySelector("#role-template-wrapper");

const createRole = (() => {
  const elm = roleTemplateWrapper.querySelector("select");
  if (elm && elm.tagName === "SELECT") {
    return function(data) {
      const select = roleTemplateWrapper.firstElementChild.cloneNode(true);
      const option = select.querySelector(`option[value=${data}]`);
      option.setAttribute("selected", "selected");
      return select.outerHTML;
    };
  }
  return data => {
    return roleTemplateWrapper.querySelector(`.${data}`).outerHTML;
  };
})();

const COLUMNS = generateColumnOrderInfo();
const CONFIG = Object.assign({}, tableConfig, {
  ajax: $table.data("url"),
  columnDefs: [
    {
      targets: [COLUMNS.OBJECT_USERNAME],
      render(data, type, full) {
        return createItemLink({
          url: `${window.PAGE.urls.usersLink}${full.object.identifier}`,
          label: data
        });
      }
    },
    {
      targets: [COLUMNS.CREATED_DATE],
      render(data) {
        return `<time>${formatDate({ date: data })}</time>`;
      }
    },
    {
      targets: [COLUMNS.PROJECT_ROLE],
      render: createRole
    },
    {
      targets: -1,
      render() {
        const deleteBtn = createDeleteBtn({
          title: window.PAGE.i18n.remove
        });
        return createButtonCell([deleteBtn]);
      }
    }
  ],
  createdRow(row, data) {
    const $row = $(row);
    $row.data("user", data.object.identifier);
    $row.tooltip({ selector: "[data-toggle='tooltip']" });
  }
});

/*
Initialize the DataTable
 */
const $dt = $table.DataTable(CONFIG);
// Add custom buttons for adding a new member
const $memberBtn = $("#table-toolbar");
// Only admins will have these buttons.
if ($memberBtn) {
  $("#toolbar-wrapper").remove();
  const $btnDiv = $("#usersTable_wrapper").find(".buttons");
  $btnDiv.html($memberBtn);
}

$table
  .on("focus", "select", function() {
    const $elm = $(this);
    $elm.data("prev", $elm.val());
  })
  .on("change", "select", function(e) {
    const $select = $(this);

    $.ajax({
      url: `${window.PAGE.urls.updateRole}${$select.data("userId")}`,
      type: "POST",
      data: {
        projectRole: $select.val()
      },
      success(result) {
        if (result.success) {
          window.notifications.show({ msg: result.success });
        } else if (result.failure) {
          $select.val($select.data("prev"));
          window.notifications.show({
            msg: result.failure,
            type: "error"
          });
        }
      }
    });
  })
  .on("click", ".remove-btn", function() {
    const memberId = $(this)
      .closest("tr")
      .data("user");

    $("#removeMemberModal").load(
      `${window.PAGE.urls.deleteModal}#removeMemberModalGen`,
      { memberId },
      function() {
        const modal = $(this);
        modal.on("show.bs.modal", function() {
          $(this)
            .find("#remove-member-button")
            .off("click")
            .click(function() {
              $.ajax({
                url: `${window.PAGE.urls.removeMember}${memberId}`,
                type: "DELETE",
                success: function(result) {
                  if (result.success) {
                    $dt.ajax.reload();
                    window.notifications.show({
                      msg: result.success
                    });
                  } else if (result.failure) {
                    window.notifications.show({
                      msg: result.failure,
                      type: "error"
                    });
                  }
                  modal.modal("hide");
                }
              });
            });
        });
        modal.modal("show");
      }
    );
  });

$("#add-member-membername").select2({
  theme: "bootstrap",
  width: "100%",
  minimumInputLength: 1,
  ajax: {
    url: window.PAGE.urls.usersSelection,
    data(params) {
      return {
        term: params.term,
        page_limit: 10
      };
    },
    processResults(data) {
      return {
        results: data.map(function(el) {
          return { id: el["identifier"], text: el["label"] };
        })
      };
    }
  }
});

$("#submitAddMember").on("click", function() {
  $.ajax({
    url: window.PAGE.urls.addMember,
    method: "POST",
    data: {
      memberId: $("#add-member-membername").val(),
      projectRole: $("#add-member-role").val()
    },
    success(result) {
      $("#addMemberModal").modal("hide");
      $dt.ajax.reload();
      window.notifications.show({
        msg: result.result
      });
      $("#add-member-membername").select2("val", "");
    },
    error() {
      $("#addMemberModal").modal("hide");
      window.notifications.show({
        msg: window.PAGE.i18n.unexpectedAddError,
        type: "error"
      });
    }
  });
});
