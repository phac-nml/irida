/*
 * This file renders the Remote Api component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */

import React from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { RemoteApiTable } from "../../remote-apis/RemoteApiTable";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { PagedTableProvider } from "../../../components/ant.design/PagedTable";

export default function AdminRemoteApiPage() {
  // The following renders the Remote Api component view
  return (
    <PageWrapper
      title={i18n("AdminPanel.remoteApi")}
      headerExtras={
        <AddNewButton
          text={i18n("AdminPanel.addRemoteApi")}
          href={setBaseUrl("remote_api/create")}
        />
      }
    >
      <PagedTableProvider url="ajax/remote_api/list">
        <RemoteApiTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}