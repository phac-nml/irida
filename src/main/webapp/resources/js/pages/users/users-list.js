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
import { Button, Checkbox } from "antd";
import { EditOutlined } from "@ant-design/icons";
import { setUsersDisabledStatus } from "../../apis/users/users";

function UsersTable() {
  const { updateTable } = useContext(PagedTableContext);

  function updateUser(user) {
    setUsersDisabledStatus({
      isEnabled: !user.enabled,
      id: user.id
    }).then(updateTable);
  }

  const columns = [
    {
      title: "Enabled",
      dataIndex: "enabled",
      key: "enabled",
      fixed: "left",
      sorter: true,
      render(text, full) {
        return (
          <Checkbox checked={full.enabled} onChange={() => updateUser(full)} />
        );
      }
    },
    {
      title: i18n("users.username"),
      key: "username",
      dataIndex: "name",
      sorter: true,
      fixed: "left",
      render(text, full) {
        return <a href={setBaseUrl(`users/${full.id}`)}>{text}</a>;
      }
    },
    {
      title: i18n("users.firstName"),
      key: "firstName",
      sorter: true,
      dataIndex: "firstName"
    },
    {
      title: i18n("users.lastName"),
      key: "lastName",
      sorter: true,
      dataIndex: "lastName"
    },
    {
      title: i18n("users.email"),
      key: "email",
      dataIndex: "email",
      render(text, full) {
        return <a href={`mailto:${text}`}>{text}</a>;
      }
    },
    {
      title: i18n("users.role"),
      key: "role",
      dataIndex: "role",
      sorter: true,
      render(text) {
        switch (text) {
          case "ROLE_USER":
            return i18n("systemrole.ROLE_USER");
          case "ROLE_MANAGER":
            return i18n("systemrole.ROLE_MANAGER");
          case "ROLE_ADMIN":
            return i18n("systemrole.ROLE_ADMIN");
          case "ROLE_TECHNICIAN":
            return i18n("systemrole.ROLE_TECHNICIAN");
          default:
            return text;
        }
      }
    },
    {
      ...dateColumnFormat(),
      key: "createdDate",
      title: i18n("users.created"),
      dataIndex: "createdDate"
    },
    {
      ...dateColumnFormat(),
      key: "lastLogin",
      title: i18n("users.last-login"),
      dataIndex: "lastLogin"
    },
    {
      fixed: "right",
      key: "edit",
      render(text, item) {
        return (
          <Button shape="circle" href={setBaseUrl(`users/${item.id}/edit`)}>
            <EditOutlined />
          </Button>
        );
      }
    }
  ];

  return (
    <PagedTable
      columns={columns}
      onRow={record => (record.enabled ? {} : { className: "disabled" })}
    />
  );
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
