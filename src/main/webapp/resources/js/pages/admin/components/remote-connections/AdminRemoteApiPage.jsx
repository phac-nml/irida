/*
 * This file renders the Remote Api component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */

import React from "react";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";
import { PageWrapper } from "../../../../components/page/PageWrapper";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { CreateRemoteApiModal } from "../../../remote-apis/CreateRemoteApiModal";
import { RemoteApiTable } from "./RemoteApiTable";

export default function AdminRemoteApiPage() {
  // The following renders the Remote Api component view
  return (
    <PagedTableProvider url={setBaseUrl("ajax/remote_api/list")}>
      <PageWrapper
        title={i18n("AdminRemoteApiPage.title")}
        headerExtras={
          <CreateRemoteApiModal>
            <AddNewButton text={i18n("AdminRemoteApiPage.addRemoteApi")} />
          </CreateRemoteApiModal>
        }
      >
        <RemoteApiTable />
      </PageWrapper>
    </PagedTableProvider>
  );
}
