import React from "react";
import { render } from "react-dom";
import { Typography } from "antd";
import { NcbiExportTable } from "../../../components/ncbi/export-table/NcbiExportTable";
import { setBaseUrl } from "../../../utilities/url-utilities";

const { Title } = Typography;

function ProjectNcbiExportsPage() {
  return (
    <>
      <Title level={2}>NCBI EXPORTS</Title>
      <NcbiExportTable
        url={setBaseUrl(`/ajax/ncbi/project/${window.project.id}/list`)}
      />
    </>
  );
}

render(<ProjectNcbiExportsPage />, document.querySelector("#root"));
