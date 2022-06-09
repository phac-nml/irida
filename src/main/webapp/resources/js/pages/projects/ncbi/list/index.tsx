import * as React from "react";
import {Typography} from "antd";
import {NcbiExportTable} from "../../../../components/ncbi/export-table/NcbiExportTable";

function NCBIExportsPage() : JSX.Element {
   return (<>
￼      <Typography.Title level={2}>{i18n("NcbiExportPage.title")}</Typography.Title>
￼      <NcbiExportTable />
￼    </>);
}