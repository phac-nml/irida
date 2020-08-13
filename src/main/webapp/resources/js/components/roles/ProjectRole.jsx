import React, { useState } from "react";
import { notification, Select } from "antd";
import { RolesContext, useRoles } from "../../contexts/roles-context";

/**
 * React component to render the project role.  If the user can manage members,
 * then a select component will be rendered allowing the user to change the role
 * any member.  If the user cannot manage, just the label for the project role
 * will be rendered
 *
 * @param {object} user - the current user to be rendered
 * @returns {*}
 * @constructor
 */
export function ProjectRole({
  item,
  // eslint-disable-next-line no-console
  updateFn = () => console.error("updateFn is required"),
}) {
  const [role, setRole] = useState(item.role);
  const [loading, setLoading] = useState(false);
  const { roles, getRoleFromKey } = useRoles();

  const onChange = (value) => {
    setLoading(true);
    updateFn({
      id: item.id,
      role: value,
    })
      .then((message) => {
        notification.success({ message });
        setRole(value);
      })
      .catch((error) =>
        notification.error({
          message: error.response.data,
        })
      )
      .finally(() => setLoading(false));
  };

  return window.PAGE.canManage ? (
    <Select
      className="t-role-select"
      value={role}
      style={{ width: "100%" }}
      onChange={onChange}
      loading={loading}
      disabled={loading}
    >
      {roles.map((role) => (
        <Select.Option
          className={`t-${role.value}`}
          value={role.value}
          key={role.value}
        >
          {role.label}
        </Select.Option>
      ))}
    </Select>
  ) : (
    getRoleFromKey(item.role)
  );
}
