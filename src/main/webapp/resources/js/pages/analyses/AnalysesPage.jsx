import React from "react";
import { render } from "react-dom";
import { AnalysesTable } from "../../components/AnalysesTable/AnalysesTable";
import { PagedTableProvider } from "../../contexts/PagedTableContext";
import { PageWrapper } from "../../components/page/PageWrapper";
import { getI18N } from "../../utilities/i18n-utilties";

render(
  <PageWrapper title={getI18N("analyses.header")}>
    <PagedTableProvider url={window.PAGE.url}>
      <AnalysesTable />
    </PagedTableProvider>
  </PageWrapper>,
  document.querySelector("#root")
);
