import { notification, Select } from "antd";
import React, { useState } from "react";

/**
 * React component to render the project role.  If the user can manage members,
 * then a select component will be rendered allowing the user to change the role
 * any member.  If the user cannot manage, just the label for the project role
 * will be rendered
 *
 * @param {object} user - the current item to be rendered
 * @returns {*}
 * @constructor
 */
export function RoleSelect({
  updateRoleFn,
  roles,
  currentRole,
  className,
  disabledProjectOwner = false,
}) {
  const [role, setRole] = React.useState("");
  const [loading, setLoading] = useState(false);

  React.useEffect(() => {
    setRole(currentRole);
  }, [currentRole]);

  /**
   * When the project role for the user is updated, update the new value on
   * the server as well.
   *
   * @param {string} value - updated role
   */
  const onChange = (value) => {
    setLoading(true);
    return updateRoleFn(value)
      .then((message) => {
        notification.success({ message });
        setRole(value);
      })
      .catch((message) =>
        notification.error({
          message,
        })
      )
      .finally(() => setLoading(false));
  };

  return (
    <Select
      className={className}
      value={role}
      style={{ width: "100%" }}
      onChange={onChange}
      loading={loading}
      disabled={loading || disabledProjectOwner}
    >
      {roles?.map((role) => (
        <Select.Option
          className={`t-${role.value}`}
          value={role.value}
          key={role.value}
        >
          {role.label}
        </Select.Option>
      ))}
    </Select>
  );
}
