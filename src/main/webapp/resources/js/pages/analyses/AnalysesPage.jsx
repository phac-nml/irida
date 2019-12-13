import React from "react";
import { render } from "react-dom";
import { AnalysesTable } from "../../components/AnalysesTable/AnalysesTable";
import { AnalysisServiceStatus } from "./AnalysisServiceStatus";
import { PagedTableProvider } from "../../contexts/PagedTableContext";
import { PageWrapper } from "../../components/page/PageWrapper";

render(
  <PageWrapper title={i18n("analyses.header")}>
    <AnalysisServiceStatus running={5} queued={15}/>
    <PagedTableProvider url={window.PAGE.url}>
      <AnalysesTable />
    </PagedTableProvider>
  </PageWrapper>,
  document.querySelector("#root")
);
