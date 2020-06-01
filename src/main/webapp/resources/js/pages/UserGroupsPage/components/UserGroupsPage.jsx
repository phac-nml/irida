import { PageWrapper } from "../../../components/page/PageWrapper";
import { PagedTableProvider } from "../../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { UserGroupsTable } from "./UserGroupsTable";
import React from "react";

export default function UserGroupsPage() {
  return (
    <PageWrapper title={i18n("UserGroupsPage.title")}>
      <PagedTableProvider url={setBaseUrl(`/ajax/user-groups/list`)}>
        <UserGroupsTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}
