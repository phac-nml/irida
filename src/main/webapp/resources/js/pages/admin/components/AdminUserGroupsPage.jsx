import React, { lazy } from "react";
import { Router } from "@reach/router";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { RolesProvider } from "../../../contexts";
import { getUserGroupRoles } from "../../../apis/users/groups";
import { UserGroupsProvider } from "../../../contexts/UserGroupsContext";

const UserGroupsPage = lazy(() => import("../../UserGroupsPage/components/UserGroupsPage"));
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
      <RolesProvider rolesFn={getUserGroupRoles}>
        <Router>
          <UserGroupsPage baseUrl={DEFAULT_URL} path={"/"} />
          <UserGroupsDetailsPage baseUrl={DEFAULT_URL} path={"/:id"} />
        </Router>
      </RolesProvider>
    </UserGroupsProvider>
  );
}