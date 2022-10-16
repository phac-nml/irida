import React from "react";
import { createRoot } from "react-dom/client";
import { AnalysesTable } from "../../components/AnalysesTable/AnalysesTable";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { PageWrapper } from "../../components/page/PageWrapper";
import { AnalysesTableProvider } from "../../contexts/AnalysesTableContext";

const root = createRoot(document.querySelector("#root"));
root.render(
  <PageWrapper title={i18n("analyses.header")}>
    <PagedTableProvider url={window.PAGE.url}>
      <AnalysesTableProvider>
        <AnalysesTable canManage={window.PAGE.canManage} />
      </AnalysesTableProvider>
    </PagedTableProvider>
  </PageWrapper>
);
