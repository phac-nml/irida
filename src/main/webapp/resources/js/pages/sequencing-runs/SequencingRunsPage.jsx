import React from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../components/page/PageWrapper";
import { PagedTableProvider } from "../../contexts/PagedTableContext";
import { SequencingRunsList } from "./SequencingRunsList";

render(
  <PageWrapper title={i18n("sequencingruns.title")}>
    <PagedTableProvider url={`ajax/sequencingRuns/list`}>
      <SequencingRunsList />
    </PagedTableProvider>
  </PageWrapper>,
  document.querySelector("#root")
);
