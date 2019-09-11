import React from "react";
import { render } from "react-dom";
import { AnalysesTable } from "../../components/AnalysesTable/AnalysesTable";
import { AnalysesProvider } from "../../contexts/AnalysesContext";
import { PageWrapper } from "../../components/page/PageWrapper";
import { getI18N } from "../../utilities/i18n-utilties";

render(
  <AnalysesProvider>
    <PageWrapper title={getI18N("analyses.header")}>
      <AnalysesTable />
    </PageWrapper>
  </AnalysesProvider>,
  document.querySelector("#root")
);
