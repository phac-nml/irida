import React from "react";
import { Outlet } from "react-router-dom";
import { getUserGroupRoles } from "../../../apis/users/groups";
import { RolesProvider } from "../../../contexts/roles-context";
import { UserGroupsProvider } from "../../../contexts/UserGroupsContext";

/**
 * React component to wrap providers around the content within the admin panel.
 * @returns {JSX.Element}
 * @constructor
 */
export function AdminContent() {
  return (
    <UserGroupsProvider>
      <RolesProvider getRolesFn={getUserGroupRoles}>
        <Outlet />
      </RolesProvider>
    </UserGroupsProvider>
  );
}
