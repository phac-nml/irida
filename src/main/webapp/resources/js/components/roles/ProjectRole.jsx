import { notification, Select } from "antd";
import React, { useState } from "react";
import { useGetProjectDetailsQuery } from "../../apis/projects/project";
import { useProjectRoles } from "../../contexts/project-roles-context";

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
export function ProjectRole({ projectId, item, updateRoleFn }) {
  const { data: project = {} } = useGetProjectDetailsQuery(projectId);
  const [role, setRole] = React.useState(item.projectRole);
  const [loading, setLoading] = useState(false);
  const { roles, getRoleFromKey } = useProjectRoles();

  /**
   * When the project role for the user is updated, update the new value on
   * the server as well.
   *
   * @param {string} value - updated role
   */
  const onChange = (value) => {
    setLoading(true);
    updateRoleFn(value)
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

  return project.canManageRemote ? (
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
