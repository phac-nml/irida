import React, { useContext } from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../components/page/PageWrapper";
import {
  PagedTable,
  PagedTableContext,
  PagedTableProvider
} from "../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import { AddNewButton } from "../../components/Buttons/AddNewButton";
import { dateColumnFormat } from "../../components/ant.design/table-renderers";
import { Checkbox } from "antd";
import { UserRoleSelect } from "./UserRoleSelect";

function UsersTable() {
  const { updateTable } = useContext(PagedTableContext);

  const columns = [
    {
      title: "",
      fixed: "left",
      render(text, full) {
        // TODO: Make this a component that will update the server.
        return <Checkbox checked={full.enabled} />;
      }
    },
    {
      title: i18n("users.username"),
      dataIndex: "name",
      fixed: "left",
      render(text, full) {
        return <a href={setBaseUrl(`users/${full.id}`)}>{text}</a>;
      }
    },
    {
      title: i18n("users.firstName"),
      dataIndex: "firstName"
    },
    {
      title: i18n("users.lastName"),
      dataIndex: "lastName"
    },
    {
      title: i18n("users.email"),
      dataIndex: "email",
      render(text, full) {
        return <a href={`mailto:${text}`}>{text}</a>;
      }
    },
    {
      title: i18n("users.role"),
      dataIndex: "role",
      width: 150,
      render(text) {
        return <UserRoleSelect role={text} />;
      }
    },
    {
      ...dateColumnFormat(),
      title: i18n("users.created"),
      dataIndex: "createdDate"
    },
    {
      ...dateColumnFormat(),
      title: i18n("users.last-login"),
      dataIndex: "lastLogin"
    }
  ];

  return <PagedTable columns={columns} />;
}

function UsersPage() {
  return (
    <PageWrapper
      title={i18n("UsersPage.title")}
      headerExtras={
        <AddNewButton
          href={setBaseUrl(`users/create`)}
          text={i18n("UsersPage.add")}
        />
      }
    >
      <PagedTableProvider url={setBaseUrl("ajax/users/list")}>
        <UsersTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}

render(<UsersPage />, document.querySelector("#react-root"));

// import "../../vendor/datatables/datatables";
// import {
//   createItemLink,
//   generateColumnOrderInfo,
//   tableConfig
// } from "../../utilities/datatables-utilities";
// import { formatDate } from "../../utilities/date-utilities";
//
// /*
// Get the table headers and create a look up table for them.
// This give the row name in snake case and its index.
//  */
// let COLUMNS = generateColumnOrderInfo();
//
// const config = Object.assign(tableConfig, {
//   ajax: window.PAGE.urls.table,
//   order: [[COLUMNS.USERNAME, "desc"]],
//   columnDefs: [
//     {
//       targets: [COLUMNS.USERNAME],
//       render(data, type, full) {
//         return createItemLink({
//           url: `${window.PAGE.urls.link}${full.id}`,
//           label: data
//         });
//       }
//     },
//     {
//       targets: [COLUMNS.EMAIL],
//       render(data) {
//         return `<a href="mailto:${data}" class="btn btn-link">${data}</a>`;
//       }
//     },
//     {
//       targets: [COLUMNS.CREATED_DATE],
//       render(data) {
//         const date = formatDate({ date: data });
//         return `<time>${date}</time>`;
//       }
//     },
//     {
//       targets: [COLUMNS.LAST_LOGIN],
//       render(data) {
//         if (data != null) {
//           const date = formatDate({ date: data });
//           return `<time class="last-login">${date}</time>`;
//         } else {
//           return "";
//         }
//       }
//     }
//   ]
// });
//
// $("#usersTable").DataTable(config);
