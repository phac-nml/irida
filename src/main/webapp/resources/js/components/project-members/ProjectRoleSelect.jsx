import React, { useState } from "react";
import { updateUserRoleOnProject } from "../../apis/projects/members";
import { notification, Select } from "antd";

export function ProjectRoleSelect({ user }) {
  const [role, setRole] = useState(user.role);
  const [loading, setLoading] = useState(false);

  const ROLES = {
    PROJECT_USER: i18n("projectRole.PROJECT_USER"),
    PROJECT_OWNER: i18n("projectRole.PROJECT_OWNER")
  };

  const onChange = value => {
    setLoading(true);
    updateUserRoleOnProject({
      id: user.id,
      role: value
    })
      .then(message => {
        notification.success({ message });
        setRole(value);
      })
      .catch(error =>
        notification.error({
          message: error.response.data
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
      {Object.keys(ROLES).map(key => (
        <Select.Option value={key} key={key}>
          {ROLES[key]}
        </Select.Option>
      ))}
    </Select>
  );
}
