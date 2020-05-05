import React from "react";
import { render } from "react-dom";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { UserGroupsTable } from "./components/UserGroupsTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import { Router } from "@reach/router";
import { PageWrapper } from "../../components/page/PageWrapper";
import { UserGroupDetails } from "../UserGroupDetailsPage";

export function UserGroups() {
  return (
    <PageWrapper title={"User Groups"}>
      <PagedTableProvider url={setBaseUrl(`/ajax/user-groups/list`)}>
        <UserGroupsTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}

function UserGroupsPage() {
  return (
    <Router style={{ height: "100%" }}>
      <UserGroups path="/groups" />
      <UserGroupDetails path="/groups/:id" />
    </Router>
  );
}

render(<UserGroupsPage />, document.querySelector("#groups-root"));
