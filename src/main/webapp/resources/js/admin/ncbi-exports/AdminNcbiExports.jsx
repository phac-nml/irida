import React from "react";
import { render } from "react-dom";
import { Typography } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { NcbiExportTable } from "../../components/ncbi/export-table/NcbiExportTable";

const { Title } = Typography;

function AdminNcbiExportsPage() {
  return (
    <>
      <Title level={2}>ADMIN NCBI EXPORTS</Title>
      <NcbiExportTable url={setBaseUrl(`/ajax/ncbi/list`)} />
    </>
  );
}

render(<AdminNcbiExportsPage />, document.querySelector("#root"));
