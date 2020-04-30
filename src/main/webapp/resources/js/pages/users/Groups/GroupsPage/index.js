import React from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../../../components/page/PageWrapper";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import GroupsTable from "./GroupsTable";

/**
 * React component to render the User Groups Listing Page
 * @returns {*}
 * @constructor
 */
export function UserGroupsPage() {
  return (
    <PageWrapper title={"USER GROUPS"}>
      <PagedTableProvider url={setBaseUrl(`/ajax/groups`)}>
        <GroupsTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}

render(<UserGroupsPage />, document.querySelector("#groups-root"));
