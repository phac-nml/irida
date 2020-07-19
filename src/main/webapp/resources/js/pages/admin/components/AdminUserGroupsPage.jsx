import React, { lazy } from "react";
import { Router } from "@reach/router";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { RolesProvider } from "../../../contexts";
import { getUserGroupRoles } from "../../../apis/users/groups";

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
  return (
      <RolesProvider rolesFn={getUserGroupRoles}>
        <Router>
          <UserGroupsPage baseUrl={"/admin/groups"} path={setBaseUrl("")} />
          <UserGroupsDetailsPage baseUrl={"/admin/groups"} path={setBaseUrl("/:id")} />
        </Router>
      </RolesProvider>
  );
}