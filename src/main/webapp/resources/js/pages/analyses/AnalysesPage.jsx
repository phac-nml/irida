import React from "react";
import { render } from "react-dom";
import { AnalysesTable } from "../../components/AnalysesTable/AnalysesTable";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { PageWrapper } from "../../components/page/PageWrapper";
import { AnalysesTableProvider } from "../../contexts/AnalysesTableContext";

render(
  <PageWrapper title={i18n("analyses.header")}>
    <PagedTableProvider url={window.PAGE.url}>
      <AnalysesTableProvider>
        <AnalysesTable />
      </AnalysesTableProvider>
    </PagedTableProvider>
  </PageWrapper>,
  document.querySelector("#root")
);
