import React from "react";
import { render } from "react-dom";
import { Typography } from "antd";
import { NcbiExportTable } from "../../../components/ncbi/export-table/NcbiExportTable";

const { Title } = Typography;

/**
 * Render NCBI Export listing for a project page.
 * @returns {JSX.Element}
 * @constructor
 */
function ProjectNcbiExportsPage() {
  return (
    <>
      <Title level={2}>{i18n("NcbiExportPage.title")}</Title>
      <NcbiExportTable />
    </>
  );
}

render(<ProjectNcbiExportsPage />, document.querySelector("#root"));
