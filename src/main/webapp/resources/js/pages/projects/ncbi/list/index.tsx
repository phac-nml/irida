import * as React from "react";
import { PageHeader, Typography } from "antd";
import { NcbiExportTable } from "../../../../components/ncbi/export-table/NcbiExportTable";

function NCBIExportsPage(): JSX.Element {
  return (
    <>
      <PageHeader title={i18n("NcbiExportPage.title")} />
      ￼ <NcbiExportTable />
    </>
  );
}
