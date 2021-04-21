import { unwrapResult } from "@reduxjs/toolkit";
import { notification, Select } from "antd";
import React, { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useRoles } from "../../contexts/roles-context";
import { updateMemberRole } from "../../pages/projects/redux/membersSlice";

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
export function ProjectRole({ item }) {
  const dispatch = useDispatch();
  const { canManage } = useSelector((state) => state.project);
  const { roles, getRoleFromKey } = useRoles();
  const [role, setRole] = React.useState(item.role);
  const [loading, setLoading] = useState(false);

  const onChange = (value) => {
    setLoading(true);
    dispatch(updateMemberRole({ id: item.id, role: value }))
      .then(unwrapResult)
      .then(({ message }) => {
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
  ) : (
    getRoleFromKey(item.role)
  );
}
