import React from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../components/page/PageWrapper";
import { RemoteApiTable } from "./RemoteApiTable";
import { PagedTableProvider } from "../../contexts/PagedTableContext";
import { AddNewButton } from "../../components/Buttons/AddNewButton";

export function RemoteApiPage({}) {
  return (
    <PageWrapper title={i18n("RemoteApi.title")} headerExtras={<AddNewButton text={"FOOBAR"} />}>
      <PagedTableProvider url="remote_api/ajax/list">
        <RemoteApiTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}

render(<RemoteApiPage />, document.querySelector("#remote-api-list"));
