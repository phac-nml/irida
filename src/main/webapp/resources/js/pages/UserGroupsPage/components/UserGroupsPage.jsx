import { PageWrapper } from "../../../components/page/PageWrapper";
import { PagedTableProvider } from "../../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { UserGroupsTable } from "./UserGroupsTable";
import React from "react";

/**
 * React component to display a page for viewing User Groups List.
 * @param baseUrl - either /admin/groups for admin panel or /groups for main app
 * baseUrl should already be set in parent component
 * @returns {*}
 * @constructor
 */
export default function UserGroupsPage({ baseUrl }) {
  return (
    <PageWrapper title={i18n("UserGroupsPage.title")}>
      <PagedTableProvider url={setBaseUrl(`/ajax/user-groups/list`)}>
        <UserGroupsTable baseUrl={baseUrl} />
      </PagedTableProvider>
    </PageWrapper>
  );
}
