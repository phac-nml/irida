/*
 * This file renders the Users component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */

import React from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { PagedTableProvider } from "../../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { UsersTable } from "../../../components/UsersTable/UsersTable";

export default function AdminUsersPage() {
  // The following renders the Users component view
  return (
    <PageWrapper
      title={i18n("AdminPanel.users")}
      headerExtras={
        <AddNewButton
          className={"t-add-user-btn"}
          href={setBaseUrl(`users/create`)}
          text={i18n("AdminPanel.addUser")}
        />
      }
    >
      <PagedTableProvider url={setBaseUrl("ajax/users/list")}>
        <UsersTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}
