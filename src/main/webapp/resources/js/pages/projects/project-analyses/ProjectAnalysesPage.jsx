import React from "react";
import { render } from "react-dom";
import { AnalysesTable } from "../../../components/AnalysesTable/AnalysesTable";
import { PagedTableProvider } from "../../../contexts/PagedTableContext";

render(
  <PagedTableProvider url={`${window.PAGE.url}`}>
    <AnalysesTable />
  </PagedTableProvider>,
  document.querySelector("#root")
);
