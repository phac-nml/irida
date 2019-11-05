import React from "react";
import { render } from "react-dom";
import { AnalysesTable } from "../../components/AnalysesTable/AnalysesTable";
import { PagedTableProvider } from "../../contexts/PagedTableContext";
import { PageWrapper } from "../../components/page/PageWrapper";

render(
  <PageWrapper title={i18n("analyses.header")}>
    <PagedTableProvider url={window.PAGE.url}>
      <AnalysesTable />
    </PagedTableProvider>
  </PageWrapper>,
  document.querySelector("#root")
);
