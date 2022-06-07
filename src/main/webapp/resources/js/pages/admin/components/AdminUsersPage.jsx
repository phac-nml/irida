/**
 * React component to display the admin users table.
 * @returns {*}
 * @constructor
 */
import React from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { PagedTableProvider } from "../../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { UserTable } from "../../user/components/UserTable";
import CreateNewUser from "./user/CreateNewUser";

export default function AdminUsersPage() {
  return (
    <PagedTableProvider url={setBaseUrl("ajax/users/list")}>
      <PageWrapper
        title={i18n("AdminPanel.users")}
        headerExtras={<CreateNewUser />}
      >
        <UserTable />
      </PageWrapper>
    </PagedTableProvider>
  );
}
