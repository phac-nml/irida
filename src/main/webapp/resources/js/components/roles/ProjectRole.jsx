import { notification, Select } from "antd";
import React, { useState } from "react";
import { useSelector } from "react-redux";
import { updateUserRoleOnProject } from "../../apis/projects/members";
import { useRoles } from "../../contexts/roles-context";

/**
 * React component to render the project role.  If the user can manage members,
 * then a select component will be rendered allowing the user to change the role
 * any member.  If the user cannot manage, just the label for the project role
 * will be rendered
 *
 * @param {object} item - the current item to be rendered
 * @returns {*}
 * @constructor
 */
export function ProjectRole({ item }) {
  const { id: projectId, canManage } = useSelector((state) => state.project);
  const [role, setRole] = React.useState(item.role);
  const [loading, setLoading] = useState(false);
  const { roles, getRoleFromKey } = useRoles();

  /**
   * When the project role for the user is updated, update the new value on
   * the server as well.
   *
   * @param {string} value - updated role
   */
  const onChange = (value) => {
    setLoading(true);
    updateUserRoleOnProject({ projectId, id: item.id, role: value })
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

  return canManage ? (
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
