import { Router } from "@reach/router";
import React, { lazy } from "react";
import { UserGroupRolesProvider } from "../../../contexts/usergroup-roles-context";
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
      <UserGroupRolesProvider>
        <Router>
          <UserGroupsPage baseUrl={DEFAULT_URL} path={"/"} />
          <UserGroupsDetailsPage baseUrl={DEFAULT_URL} path={"/:id"} />
        </Router>
      </UserGroupRolesProvider>
    </UserGroupsProvider>
  );
}
