import React from "react";
import { render } from "react-dom";
import { AnalysesProvider } from "../../../contexts/AnalysesContext";
import { AnalysesTable } from "../../../components/AnalysesTable/AnalysesTable";

function ProjectAnalysesPage() {
  return <AnalysesTable />;
}

render(
  <AnalysesProvider>
    <ProjectAnalysesPage />
  </AnalysesProvider>,
  document.querySelector("#root")
);
