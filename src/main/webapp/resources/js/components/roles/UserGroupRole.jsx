import React, { useContext, useState } from "react";
import { UserGroupRolesContext } from "../../contexts/UserGroupRolesContext";
import { notification, Select } from "antd";
import { updateUserRoleOnUserGroups } from "../../apis/users/groups";

export function UserGroupRole({ user, canManage, groupId }) {
  const [role, setRole] = useState(user.role);
  const [loading, setLoading] = useState(false);
  const { roles, getRoleFromKey } = useContext(UserGroupRolesContext);

  const onChange = (value) => {
    if (value !== user.role) {
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
