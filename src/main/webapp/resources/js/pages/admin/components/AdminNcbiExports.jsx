/*
 * This file renders the Ncbi Exports component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */

import React from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { NcbiExportTable } from "../../../components/ncbi/export-table/NcbiExportTable";

export default function AdminNcbiExportsPage() {
  // The following renders the Ncbi Exports component view
  return (
    <PageWrapper
      title={i18n("AdminPanel.ncbiExports")}
    >
      <NcbiExportTable />
    </PageWrapper>
  );
}