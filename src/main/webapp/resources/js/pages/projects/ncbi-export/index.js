import { Typography } from "antd";
import React from "react";
import { NcbiExportTable } from "../../../components/ncbi/export-table/NcbiExportTable";

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
