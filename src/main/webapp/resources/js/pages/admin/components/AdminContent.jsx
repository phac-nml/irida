import React from "react";
import { Outlet } from "react-router-dom";
import { getUserGroupRoles } from "../../../apis/users/groups";
import { UserGroupsProvider } from "../../../contexts/UserGroupsContext";
import { UserGroupRolesProvider } from "../../../contexts/usergroup-roles-context";

/**
 * React component to wrap providers around the content within the admin panel.
 * @returns {JSX.Element}
 * @constructor
 */
export function AdminContent() {
  return (
    <UserGroupsProvider>
      <UserGroupRolesProvider getRolesFn={getUserGroupRoles}>
        <Outlet />
      </UserGroupRolesProvider>
    </UserGroupsProvider>
  );
}
