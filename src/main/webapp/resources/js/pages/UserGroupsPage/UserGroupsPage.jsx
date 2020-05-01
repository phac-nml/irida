import React from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../components/page/PageWrapper";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { UserGroupsTable } from "../users/groups/UserGroupsTable";
import { setBaseUrl } from "../../utilities/url-utilities";

export function UserGroupsPage() {
  return (
    <PagedTableProvider url={setBaseUrl(`/ajax/user-groups/list`)}>
      <PageWrapper title={"GROUPS__"}>
        <UserGroupsTable />
      </PageWrapper>
    </PagedTableProvider>
  );
}

render(<UserGroupsPage />, document.querySelector("#groups-root"));
