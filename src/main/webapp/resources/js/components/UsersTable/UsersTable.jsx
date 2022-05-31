import { Checkbox } from "antd";
import React, { useContext } from "react";
import { Link, useLocation } from "react-router-dom";
import { useSetUsersDisabledStatusMutation } from "../../apis/users/users";
import { setBaseUrl } from "../../utilities/url-utilities";
import { PagedTable, PagedTableContext } from "../ant.design/PagedTable";
import { dateColumnFormat } from "../ant.design/table-renderers";

/**
 * React component for displaying paged table of all users in the system
 * @returns {string|*}
 * @constructor
 */
export function UsersTable() {
  const location = useLocation();
  const [BASE_URL] = React.useState(() =>
    location.pathname.includes(`admin`)
      ? setBaseUrl(`/admin/users`)
      : setBaseUrl(`/users`)
  );

  const { updateTable } = useContext(PagedTableContext);
  const IS_ADMIN = window.TL._USER.systemRole === "ROLE_ADMIN";
  const [setUsersDisabledStatus] = useSetUsersDisabledStatusMutation();

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
          <Link className="t-username" to={`${BASE_URL}/${full.id}`}>
            {text}
          </Link>
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
            return i18n("systemRole.ROLE_USER");
          case "ROLE_MANAGER":
            return i18n("systemRole.ROLE_MANAGER");
          case "ROLE_ADMIN":
            return i18n("systemRole.ROLE_ADMIN");
          case "ROLE_TECHNICIAN":
            return i18n("systemRole.ROLE_TECHNICIAN");
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

  return (
    <PagedTable
      columns={columns}
      onRow={(record) => (record.enabled ? {} : { className: "disabled" })}
    />
  );
}
