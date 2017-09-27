import $ from "jquery";
import "./../../vendor/plugins/jquery/select2";
import "./../../vendor/datatables/datatables";
import {
  createButtonCell,
  createDeleteBtn,
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";

const $table = $("#usersTable");
/*
Get the template for the role wrapper.  This is used to populate the member role column.
If the user is a manager or administrator, the template will contain a select input,
if the user is a collaborator, the template will have just the labels for the roles.
 */
const roleTemplateWrapper = document.querySelector("#role-template-wrapper");

const createRole = (() => {
  const elm = roleTemplateWrapper.querySelector("select");
  /*
  Determine the type of content for the role column.  Administrators have
  the ability to change the role on a particular user.
   */
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

/*
Get the current order of the columns with there label format.
 */
const COLUMNS = generateColumnOrderInfo();

/*
Create a custom DataTables configuration.
 */
const CONFIG = Object.assign({}, tableConfig, {
  ajax: $table.data("url"),
  columnDefs: [
    {
      targets: [COLUMNS.OBJECT_USERNAME],
      render(data, type, full) {
        /*
        Create a link back to the user's page.
         */
        return createItemLink({
          url: `${window.PAGE.urls.usersLink}${full.object.identifier}`,
          label: data
        });
      }
    },
    {
      targets: [COLUMNS.CREATED_DATE],
      render(data) {
        /*
        Format the date based on the standard for IRIDA.
         */
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
    Add the user id to the row so that it can be used for updating the user.
     */
    $row.data("user", data.object.identifier);
    /*
    Activate the tooltips.
     */
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

    $.ajax({
      url: `${window.PAGE.urls.updateRole}${userId}`,
      type: "POST",
      data: {
        projectRole: $select.val()
      },
      success(result) {
        if (result.success) {
          window.notifications.show({ msg: result.success });
        } else if (result.failure) {
          /*
          If failed, return the select box to it's original state, and
          show the user a notification as to what exactly happened.
           */
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

    /*
    Display the confirmation modal for removing a user from the project.
     */
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
                    /*
                    If it worked, reload to the table to remove the user from it.
                     */
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

/*
Initialize the select2 box for the user name in adding
a new user to this project.
 */
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

/*
Handle the clicking the add new member button submit.
 */
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
