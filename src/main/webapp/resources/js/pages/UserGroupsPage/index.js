import React, { lazy, Suspense } from "react";
import { render } from "react-dom";
import { Router } from "@reach/router";
import { Spin } from "antd";

const UserGroupsPage = lazy(() => import("./components/UserGroupsPage"));
const UserGroupsDetailsPage = lazy(() =>
  import("./components/UserGroupDetailsPage")
);

/**
 * React component to display pages related to User Groups
 * @returns {*}
 * @constructor
 */
export function UserGroups() {
  return (
    <Suspense
      fallback={
        <div>
          <Spin /> Fetching important data
        </div>
      }
    >
      <Router style={{ height: "100%" }}>
        <UserGroupsPage path="/groups" />
        <UserGroupsDetailsPage path="/groups/:id" />
      </Router>
    </Suspense>
  );
}

render(<UserGroups />, document.querySelector("#groups-root"));
