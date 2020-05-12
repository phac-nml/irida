import React, { lazy, Suspense } from "react";
import { render } from "react-dom";
import { Router } from "@reach/router";
import { Spin } from "antd";
import { PageLoadingIndicator } from "../../components/PageLoadingIndicator";

const UserGroupsPage = lazy(() => import("./components/UserGroupsPage"));
const UserGroupsDetailsPage = lazy(() =>
  import("./components/UserGroupDetailsPage")
);

/**
 * React component to display pages related to User Groups.  This is a base page
 * for both listing of user groups and user group details.
 * @returns {*}
 * @constructor
 */
export function UserGroups() {
  return (
    <Suspense
      fallback={<PageLoadingIndicator text={i18n("UserGroupsPage.loading")} />}
    >
      <Router style={{ height: "100%" }}>
        <UserGroupsPage path="/groups" />
        <UserGroupsDetailsPage path="/groups/:id" />
      </Router>
    </Suspense>
  );
}

render(<UserGroups />, document.querySelector("#groups-root"));
