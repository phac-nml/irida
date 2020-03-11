import React from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../components/page/PageWrapper";
import { RemoteApiTable } from "./RemoteApiTable";
import { AddNewButton } from "../../components/Buttons/AddNewButton";
import { setBaseUrl } from "../../utilities/url-utilities";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";

/**
 * React component to render Remote APIs page.
 * @returns {*}
 * @constructor
 */
export function RemoteApiPage({}) {
  return (
    <PageWrapper
      title={i18n("RemoteApi.title")}
      headerExtras={
        window.PAGE.admin ? (
          <AddNewButton
            text={i18n("remoteapi.add")}
            href={setBaseUrl("remote_api/create")}
          />
        ) : null
      }
    >
      <PagedTableProvider url="ajax/remote_api/list">
        <RemoteApiTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}

render(<RemoteApiPage />, document.querySelector("#remote-api-list"));
