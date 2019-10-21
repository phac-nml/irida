import $ from "jquery";
import "../../vendor/datatables/datatables";
import "../../vendor/plugins/jquery/select2";
import {
  createButtonCell,
  createDeleteBtn,
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";
import { showNotification } from "../../modules/notifications";

const COLUMNS = generateColumnOrderInfo();
const $table = $("#groupMembersTable");

const url = $table.data("url");
const canManage = $table.data("canmanage");

const roleTemplateWrapper = document.querySelector("#role-template-wrapper");
const createRole = (() => {
  const elm = roleTemplateWrapper.querySelector("select");
  /*
  Determine the type of content for the role column.  Administrators have
  the ability to change the role on a particular user.
   */
  if (elm && elm.tagName === "SELECT") {
    return function(data, type, full) {
      const select = roleTemplateWrapper.firstElementChild.cloneNode(true);
      select.id = `${full.id}-role-select`;
      const option = select.querySelector(`option[value=${full.role}]`);
      option.setAttribute("selected", "selected");
      return select.outerHTML;
    };
  }
  return data => {
    return roleTemplateWrapper.querySelector(`.${data}`).outerHTML;
  };
})();

/**
 * Loads a modal window to remove a user from the group.
 * @param {number} userId identifier for the user to remove.
 */
function removeUser(userId) {
  $("#removeUserModal").load(
    window.PAGE.urls.deleteModal + "#removeUserModalGen",
    { userId: userId },
    function() {
      const modal = $(this);
      modal.on("show.bs.modal", function() {
        $(this)
          .find("#remove-user-button")
          .off("click")
          .click(function() {
            $.ajax({
              url: `${window.PAGE.urls.removeMember}${userId}`,
              type: "DELETE",
              success: function(result) {
                if (result.success) {
                  $dt.ajax.reload();
                  showNotification({
                    text: result.success
                  });
                } else if (result.failure) {
                  showNotification({
                    text: result.failure,
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
}

const config = Object.assign({}, tableConfig, {
  ajax: url,
  columnDefs: [
    {
      targets: [COLUMNS.USER_USERNAME],
      width: 200,
      render(data, type, full) {
        return createItemLink({
          url: `${window.PAGE.urls.usersLink}${full.id}`,
          label: full.label,
          width: "200px"
        });
      }
    },
    {
      targets: [COLUMNS.ROLE],
      render: createRole
    },
    {
      targets: [COLUMNS.CREATED_DATE],
      render(data) {
        return `<time>${formatDate({ date: data })}</time>`;
      }
    },
    {
      targets: -1,
      render() {
        /*
        Last column is only displayed to administrators.
        This column will not be there if the user is not an administrator.
        Creates a delete button to remove the user from the project.
         */
        const deleteBtn = createDeleteBtn({
          title: window.PAGE.i18n.remove
        });
        return createButtonCell([deleteBtn]);
      }
    }
  ],
  createdRow(row, data) {
    const $row = $(row);
    /*
    Activate the tooltips.
     */
    $row.tooltip({ selector: "[data-toggle='tooltip']" });
    /*
    Set up remove listener
     */
    if (canManage) {
      $row.on("click", ".remove-btn", function() {
        removeUser(data.id);
      });
    }
  }
});

/*
Initialize the DataTable
 */
const $dt = $table.DataTable(config);
// Add custom buttons for adding a new member
const $toolbar = $("#table-toolbar");
// Only admins will have these buttons.
if ($toolbar) {
  $("#toolbar-wrapper").remove();
  const $btnDiv = $("#groupMembersTable_wrapper").find(".buttons");
  $btnDiv.html($toolbar);
}

$table
  .on("focus", "select", function() {
    /*
    When the user focus's on a select input, store the previous value in case
    the attempt to update it fails and we need to revert the select2 box back
    to its original state.
     */
    const $elm = $(this);
    $elm.data("prev", $elm.val());
  })
  .on("change", "select", function(e) {
    const $select = $(this);
    const userId = $select.closest("tr").data("user");
    const url = $select.data("updateurl");

    $.ajax({
      url: `${url}${userId}`,
      type: "POST",
      data: {
        groupRole: $select.val()
      },
      success(result) {
        if (result.success) {
          showNotification({ text: result.success });
        } else if (result.failure) {
          /*
          If failed, return the select box to it's original state, and
          show the user a notification as to what exactly happened.
           */
          $select.val($select.data("prev"));
          showNotification({
            text: result.failure,
            type: "error"
          });
        }
      }
    });
  });

/*
Initialize the select2 box for the user name in adding
a new user to this group.
 */
$("#add-user-username").select2({
  theme: "bootstrap",
  width: "100%",
  minimumInputLength: 1,
  ajax: {
    url: window.PAGE.urls.usersSelection,
    data(params) {
      return {
        term: params,
        page_limit: 10
      };
    },
    results(data) {
      return {
        results: data.map(function(el) {
          return { id: el["identifier"], text: el["label"] };
        })
      };
    }
  }
});

$("#submitAddMember").click(function() {
  $.ajax({
    url: window.PAGE.urls.addMember,
    method: "POST",
    data: {
      userId: $("#add-user-username").val(),
      groupRole: $("#add-user-role").val()
    },
    success(result) {
      $("#addUserModal").modal("hide");
      $dt.ajax.reload();
      showNotification({
        text: result.result
      });
      $("#add-user-username")
        .val("")
        .trigger("change");
    },
    error() {
      $("#addUserModal").modal("hide");
      showNotification({
        text: page.i18n.unexpectedAddError,
        type: "error"
      });
    }
  });
});
