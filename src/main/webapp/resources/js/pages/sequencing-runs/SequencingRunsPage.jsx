import React from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../components/page/PageWrapper";
import { PagedTableProvider } from "../../contexts/PagedTableContext";
import { SequencingRunsList } from "./SequencingRunsList";
import { getI18N } from "../../utilities/i18n-utilities";

render(
  <PageWrapper title={getI18N("sequencingruns.title")}>
    <PagedTableProvider url={`${window.TL.BASE_URL}ajax/sequencingRuns/list`}>
      <SequencingRunsList />
    </PagedTableProvider>
  </PageWrapper>,
  document.querySelector("#root")
);
