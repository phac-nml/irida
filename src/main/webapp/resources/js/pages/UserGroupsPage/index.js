import React, { lazy, Suspense } from "react";
import { render } from "react-dom";
import { Router } from "@reach/router";
import { ContentLoading } from "../../components/loader";
import { setBaseUrl } from "../../utilities/url-utilities";

const UserGroupsPage = lazy(() => import("./components/UserGroupsPage"));
const UserGroupsDetailsPage = lazy(() =>
  import("./components/UserGroupDetailsPage")
);

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`dist/`);

/**
 * React component to display pages related to User Groups.  This is a base page
 * for both listing of user groups and user group details.
 * @returns {*}
 * @constructor
 */
export function UserGroups() {
  return (
    <Suspense
      fallback={
        <div
          style={{
            height: "100%",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <ContentLoading message={i18n("UserGroupsPage.loading")} />
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
