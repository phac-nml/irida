import React from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../components/page/PageWrapper";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { SequencingRunsList } from "./SequencingRunsList";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`ajax/sequencingRuns`);

render(
  <PageWrapper title={i18n("sequencingruns.title")}>
    <PagedTableProvider url={`${URL}/list`}>
      <SequencingRunsList />
    </PagedTableProvider>
  </PageWrapper>,
  document.querySelector("#root")
);
