import React from "react";
import { render } from "react-dom";
import { PagedTableProvider } from "../../../contexts/PagedTableContext";
import { AnalysesTable } from "../../../components/AnalysesTable/AnalysesTable";

function ProjectAnalysesPage() {
  return <AnalysesTable />;
}

render(
  <PagedTableProvider url={`${window.TL.BASE_URL}ajax/analyses/list`}>
    <ProjectAnalysesPage />
  </PagedTableProvider>,
  document.querySelector("#root")
);
