import React, { useContext, useState } from "react";
import { updateUserRoleOnProject } from "../../apis/projects/members";
import { notification, Select } from "antd";
import { ProjectRolesContext } from "../../contexts/ProjectRolesContext";

export function ProjectRoleSelect({ user }) {
  const [role, setRole] = useState(user.role);
  const [loading, setLoading] = useState(false);
  const { roles } = useContext(ProjectRolesContext);

  const onChange = (value) => {
    setLoading(true);
    updateUserRoleOnProject({
      id: user.id,
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

  return (
    <Select
      value={role}
      style={{ width: "100%" }}
      onChange={onChange}
      loading={loading}
      disabled={loading || user.id === window.PAGE.user}
    >
      {roles.map((role) => (
        <Select.Option value={role.value} key={role.value}>
          {role.label}
        </Select.Option>
      ))}
    </Select>
  );
}
