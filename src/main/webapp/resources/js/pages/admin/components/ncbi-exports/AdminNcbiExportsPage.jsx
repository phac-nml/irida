/*
 * This file renders the Ncbi Exports component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */

import React from "react";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import AdminNcbiExportsTable from "./AdminNcbiExportsTable";
import { PageWrapper } from "../../../../components/page/PageWrapper";

/**
 * React component to render the Page for Admin NCBI Exports
 * @returns {JSX.Element}
 * @constructor
 */
export default function AdminNcbiExportsPage() {
  // The following renders the Ncbi Exports component view
  return (
    <PageWrapper title={i18n("AdminPanel.ncbiExports")}>
      <PagedTableProvider url={setBaseUrl(`/ajax/ncbi/list`)}>
        <AdminNcbiExportsTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}
