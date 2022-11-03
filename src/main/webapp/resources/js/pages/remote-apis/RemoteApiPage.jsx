import React from "react";
import { createRoot } from "react-dom/client";
import { PageWrapper } from "../../components/page/PageWrapper";
import { RemoteApiTable } from "../admin/components/remote-connections/RemoteApiTable";
import { AddNewButton } from "../../components/Buttons/AddNewButton";
import { setBaseUrl } from "../../utilities/url-utilities";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { CreateRemoteApiModal } from "./CreateRemoteApiModal";
import { isAdmin } from "../../utilities/role-utilities";

/**
 * React component to render Remote APIs page.
 * @returns {*}
 * @constructor
 */
export function RemoteApiPage({}) {
  return (
    <PagedTableProvider url={setBaseUrl("ajax/remote_api/list")}>
      <PageWrapper
        title={i18n("RemoteApi.title")}
        headerExtras={
          isAdmin() ? (
            <CreateRemoteApiModal>
              <AddNewButton text={i18n("RemoteApi.add")} />
            </CreateRemoteApiModal>
          ) : null
        }
      >
        <RemoteApiTable />
      </PageWrapper>
    </PagedTableProvider>
  );
}

const ROOT_ELEMENT = document.querySelector("#remote-api-list");
const root = createRoot(ROOT_ELEMENT);
root.render(<RemoteApiPage />);
