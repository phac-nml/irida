import React from "react";
import {
  PagedTable,
  PagedTableContext,
} from "../../../components/ant.design/PagedTable";
import { Checkbox } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { dateColumnFormat } from "../../../components/ant.design/table-renderers";
import { useSetUserStatusMutation } from "../../../apis/users/users";
import { Link } from "react-router-dom";

const BASE_URL = setBaseUrl("admin/users");

/**
 * React component for displaying paged table of all users in the system
 * @returns {string|*}
 * @constructor
 */
export function UserTable() {
  const { updateTable } = React.useContext(PagedTableContext);
  const IS_ADMIN = window.TL._USER.systemRole === "ROLE_ADMIN";
  const [updateUserStatus] = useSetUserStatusMutation();

  function updateUser(user) {
    updateUserStatus({
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
        <span className="t-username-col">{i18n("UserTable.username")}</span>
      ),
      key: "username",
      dataIndex: "name",
      sorter: true,
      fixed: "left",
      render(text, full) {
        return IS_ADMIN ? (
          <Link className="t-username" to={`${BASE_URL}/${full.id}`}>
            {text}
          </Link>
        ) : (
          text
        );
      },
    },
    {
      title: i18n("UserTable.firstName"),
      key: "firstName",
      sorter: true,
      dataIndex: "firstName",
    },
    {
      title: i18n("UserTable.lastName"),
      key: "lastName",
      sorter: true,
      dataIndex: "lastName",
    },
    {
      title: i18n("UserTable.email"),
      key: "email",
      dataIndex: "email",
      render(text) {
        return <a href={`mailto:${text}`}>{text}</a>;
      },
    },
    {
      title: i18n("UserTable.phoneNumber"),
      key: "phoneNumber",
      dataIndex: "phoneNumber",
    },
    {
      title: i18n("UserTable.role"),
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
      title: i18n("UserTable.created"),
      dataIndex: "createdDate",
    },
    {
      ...dateColumnFormat({ className: "t-modified" }),
      key: "lastLogin",
      title: (
        <span className="t-modified-col">{i18n("UserTable.lastLogin")}</span>
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
