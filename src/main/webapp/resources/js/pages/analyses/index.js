import React from "react";
import { render } from "react-dom";
import { AnalysesTable } from "./AnalysesTable";
import { AnalysesProvider } from "../../contexts/AnalysesContext";

render(
  <AnalysesProvider>
    <AnalysesTable />
  </AnalysesProvider>,
  document.querySelector("#root")
);
