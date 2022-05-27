import { Typography } from "antd";
import React from "react";
import { render } from "react-dom";
import { NcbiExportTable } from "../../../components/ncbi/export-table/NcbiExportTable";
import ProjectSPA from "../ProjectSPA";

const { Title } = Typography;

/**
 * Render NCBI Export listing for a project page.
 * @returns {JSX.Element}
 * @constructor
 */
export function ProjectNcbiExportsPage() {
  return (
    <>
      <Title level={2}>{i18n("NcbiExportPage.title")}</Title>
      <NcbiExportTable />
    </>
  );
}

// TODO: This will need to be moved up as the project SPA gets created.
render(<ProjectSPA />, document.querySelector("#root"));
