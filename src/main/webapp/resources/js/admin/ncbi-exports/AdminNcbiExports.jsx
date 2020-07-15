import React from "react";
import { render } from "react-dom";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import AdminNcbiExportsTable from "./AdminNcbiExportsTable";
import { PageWrapper } from "../../components/page/PageWrapper";

/**
 * React component to render the Page for Admin NCBI Exports
 * @returns {JSX.Element}
 * @constructor
 */
function AdminNcbiExportsPage() {
  return (
    <PageWrapper title={i18n("AdminNcbiExports.title")}>
      <PagedTableProvider url={setBaseUrl(`/ajax/ncbi/list`)}>
        <AdminNcbiExportsTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}

render(<AdminNcbiExportsPage />, document.querySelector("#root"));
