import React from "react";
import { Typography } from "antd";
const { Title } = Typography;

import { AnalysesTable } from "../../../../components/AnalysesTable/AnalysesTable";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";

export default function ProjectAnalysesPage({ canManage }) {
  return (
    <PagedTableProvider url={`${window.PAGE.url}`}>
      <Title level={2}>Analyses</Title>
      <AnalysesTable canManage={canManage} />
    </PagedTableProvider>
  );
}
