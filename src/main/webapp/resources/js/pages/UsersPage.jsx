import React, { useContext } from "react";
import { render } from "react-dom";
import { PageWrapper } from "../components/page/PageWrapper";
import {
  PagedTable,
  PagedTableContext,
  PagedTableProvider,
} from "../components/ant.design/PagedTable";
import { setBaseUrl } from "../utilities/url-utilities";
import { AddNewButton } from "../components/Buttons/AddNewButton";
import { dateColumnFormat } from "../components/ant.design/table-renderers";
import { Button, Checkbox } from "antd";
import { setUsersDisabledStatus } from "../apis/users/users";
import { IconEdit } from "../components/icons/Icons";

/**
 * React component for displaying paged table of all users in the system
 * @returns {string|*}
 * @constructor
 */
function UsersTable() {
  const { updateTable } = useContext(PagedTableContext);
  const IS_ADMIN = window.TL._USER.systemRole === "ROLE_ADMIN";

  function updateUser(user) {
    setUsersDisabledStatus({
      isEnabled: !user.enabled,
      id: user.id,
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
        // Don't let the current user disabled themselves!
        const disabled =
          !IS_ADMIN || window.TL._USER.username === full.username;
        return (
          <Checkbox
            className="t-cb-enable"
            checked={full.enabled}
            onChange={() => updateUser(full)}
            disabled={disabled}
          />
        );
      },
    },
    {
      title: (
        <span className="t-username-col">
          {i18n("AdminUsersTable.username")}
        </span>
      ),
      key: "username",
      dataIndex: "name",
      sorter: true,
      fixed: "left",
      render(text, full) {
        return (
          <a className="t-username" href={setBaseUrl(`users/${full.id}`)}>
            {text}
          </a>
        );
      },
    },
    {
      title: i18n("AdminUsersTable.firstName"),
      key: "firstName",
      sorter: true,
      dataIndex: "firstName",
    },
    {
      title: i18n("AdminUsersTable.lastName"),
      key: "lastName",
      sorter: true,
      dataIndex: "lastName",
    },
    {
      title: i18n("AdminUsersTable.email"),
      key: "email",
      dataIndex: "email",
      render(text, full) {
        return <a href={`mailto:${text}`}>{text}</a>;
      },
    },
    {
      title: i18n("AdminUsersTable.role"),
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
      },
    },
    {
      ...dateColumnFormat({ className: "t-created" }),
      key: "createdDate",
      title: i18n("AdminUsersTable.created"),
      dataIndex: "createdDate",
    },
    {
      ...dateColumnFormat({ className: "t-modified" }),
      key: "lastLogin",
      title: (
        <span className="t-modified-col">
          {i18n("AdminUsersTable.lastLogin")}
        </span>
      ),
      dataIndex: "lastLogin",
    },
  ];

  if (IS_ADMIN) {
    columns.push({
      fixed: "right",
      key: "edit",
      render(text, item) {
        return (
          <Button
            className="t-edit-user"
            shape="circle"
            href={setBaseUrl(`users/${item.id}/edit`)}
          >
            <IconEdit />
          </Button>
        );
      },
    });
  }

  return (
    <PagedTable
      columns={columns}
      onRow={(record) => (record.enabled ? {} : { className: "disabled" })}
    />
  );
}

/**
 * React component to display the page for administration of users.
 * @returns {*}
 * @constructor
 */
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
