import React from "react";
import { render } from "react-dom";
import { Typography } from "antd";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import AdminNcbiExportsTable from "./AdminNcbiExportsTable";
import { PageWrapper } from "../../components/page/PageWrapper";

const { Title } = Typography;

function AdminNcbiExportsPage() {
  return (
    <PageWrapper title={"NCBI Exports"}>
      <PagedTableProvider url={setBaseUrl(`/ajax/ncbi/list`)}>
        <AdminNcbiExportsTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}

render(<AdminNcbiExportsPage />, document.querySelector("#root"));
