import React, { useContext, useState } from "react";
import { UserGroupRolesContext } from "../../contexts/UserGroupRolesContext";
import { notification, Select } from "antd";
import { updateUserRoleOnUserGroups } from "../../apis/users/groups";

/**
 * React component to either display the users role, or to provide an input
 * for updating User Group roles if the current use can manage them.
 * @param {object} user - the user the roles are for
 * @param {boolean} canManage - whether the current user can manage this user
 * @param {number} groupId - identifier for the user group
 * @returns {*}
 * @constructor
 */
export function UserGroupRole({ user, canManage, groupId }) {
  const [role, setRole] = useState(user.role);
  const [loading, setLoading] = useState(false);
  const { roles, getRoleFromKey } = useContext(UserGroupRolesContext);

  /**
   * When the role changes update the server to keep the state.
   * @param value
   */
  const onChange = (value) => {
    if (value !== role) {
      setLoading(true);
      updateUserRoleOnUserGroups({ groupId, userId: user.id, role: value })
        .then((message) => {
          notification.success({ message });
          setRole(value);
        })
        .catch((error) => {
          notification.error({ message: error.response.data });
        })
        .finally(() => setLoading(false));
    }
  };

  return canManage ? (
    <Select
      value={role}
      style={{ width: "100%" }}
      loading={loading}
      disabled={loading}
      onChange={onChange}
    >
      {roles.map((role) => (
        <Select.Option key={role.value} value={role.value}>
          {role.label}
        </Select.Option>
      ))}
    </Select>
  ) : (
    getRoleFromKey(role)
  );
}
