import React from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../components/page/PageWrapper";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { UserGroupsTable } from "./components/UserGroupsTable";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * React component to display pages related to User Groupss
 * @returns {*}
 * @constructor
 */
export function Index() {
  return (
    <PagedTableProvider url={setBaseUrl(`/ajax/user-groups/list`)}>
      <PageWrapper title={i18n("UserGroupsPage.title")}>
        <UserGroupsTable />
      </PageWrapper>
    </PagedTableProvider>
  );
}

render(<Index />, document.querySelector("#groups-root"));
