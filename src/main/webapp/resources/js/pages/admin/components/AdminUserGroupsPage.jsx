import React, { lazy } from "react";
import { Route, Routes } from "react-router-dom";
import { getUserGroupRoles } from "../../../apis/users/groups";
import { RolesProvider } from "../../../contexts/roles-context";
import { UserGroupsProvider } from "../../../contexts/UserGroupsContext";
import { setBaseUrl } from "../../../utilities/url-utilities";

const UserGroupsPage = lazy(() =>
  import("../../UserGroupsPage/components/UserGroupsPage")
);
const UserGroupsDetailsPage = lazy(() =>
  import("../../UserGroupsPage/components/UserGroupDetailsPage")
);

/**
 * React component to display pages related to User Groups in the Admin Panel. This is a base page
 * for both listing of user groups and user group details.
 * @returns {*}
 * @constructor
 */
export default function AdminUserGroupsPage() {
  const DEFAULT_URL = setBaseUrl("/admin/groups");

  return (
    <UserGroupsProvider>
      <RolesProvider getRolesFn={getUserGroupRoles}>
        <Routes>
          <Routes>
            <Route path={`${DEFAULT_URL}/`} element={<UserGroupsPage />} />
            <Route
              path={`${DEFAULT_URL}/:id`}
              element={<UserGroupsDetailsPage />}
            />
          </Routes>
        </Routes>
      </RolesProvider>
    </UserGroupsProvider>
  );
}
