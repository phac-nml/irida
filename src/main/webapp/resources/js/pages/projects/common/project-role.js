import {
  createButtonCell,
  createDeleteBtn,
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../../utilities/datatables-utilities";
import { formatDate } from "../../../utilities/date-utilities";
import $ from "jquery";

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

export default function RolesTable(table) {
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
      $row.data("identifier", data.object.identifier);
      /*
      Activate the tooltips.
       */
      $row.tooltip({ selector: "[data-toggle='tooltip']" });
    }
  });

  /*
  Set up the toolbar
  */
  const $toolbar = $("#toolbar-wrapper");
  $(".buttons").append($toolbar.html());
  $toolbar.remove();

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
}
