import React from "react";
import { Select } from "antd";

export function UserRoleSelect({ role }) {
  return (
    <Select value={role} style={{ width: `100%` }}>
      <Select.Option value="ROLE_USER">
        {i18n("systemrole.ROLE_USER")}
      </Select.Option>
      <Select.Option value="ROLE_MANAGER">
        {i18n("systemrole.ROLE_MANAGER")}
      </Select.Option>
      <Select.Option value="ROLE_ADMIN">
        {i18n("systemrole.ROLE_ADMIN")}
      </Select.Option>
    </Select>
  );
}
