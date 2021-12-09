import React from "react";
import { Outlet } from "react-router-dom";
import { getUserGroupRoles } from "../../../apis/users/groups";
import { RolesProvider } from "../../../contexts/roles-context";
import { UserGroupsProvider } from "../../../contexts/UserGroupsContext";

export function AdminContent() {
  return (
    <UserGroupsProvider>
      <RolesProvider getRolesFn={getUserGroupRoles}>
        <Outlet />
      </RolesProvider>
    </UserGroupsProvider>
  );
}
