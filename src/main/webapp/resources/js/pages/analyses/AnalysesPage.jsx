import React from "react";
import { render } from "react-dom";
import { AnalysesTable } from "../../components/AnalysesTable/AnalysesTable";
import { AnalysesProvider } from "../../contexts/AnalysesContext";

render(
  <AnalysesProvider>
    <AnalysesTable />
  </AnalysesProvider>,
  document.querySelector("#root")
);
