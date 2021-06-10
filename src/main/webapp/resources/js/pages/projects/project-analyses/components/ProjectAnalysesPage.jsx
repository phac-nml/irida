import React from "react";
import { Typography } from "antd";
const { Title } = Typography;

import { AnalysesTable } from "../../../../components/AnalysesTable/AnalysesTable";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../../../utilities/url-utilities";

export default function ProjectAnalysesPage({ canManage, projectId }) {
  const PROJECT_ANALYSES_URL = setBaseUrl(
    `/ajax/analyses/list?projectId=${projectId}`
  );

  return (
    <PagedTableProvider url={PROJECT_ANALYSES_URL}>
      <Title level={2}>Analyses</Title>
      <AnalysesTable canManage={canManage} />
    </PagedTableProvider>
  );
}
