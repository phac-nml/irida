import { Typography } from "antd";
import React from "react";
import { useParams } from "react-router-dom";
import { useGetProjectDetailsQuery } from "../../../../apis/projects/project";
import { AnalysesTable } from "../../../../components/AnalysesTable/AnalysesTable";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { AnalysesTableProvider } from "../../../../contexts/AnalysesTableContext";
import { setBaseUrl } from "../../../../utilities/url-utilities";

const { Title } = Typography;

/**
 * React component for the overall layout for the project analyses listing table
 * @param projectId The project identifier
 * @returns {JSX.Element}
 * @constructor
 */

export default function ProjectAnalysesPage() {
  const { projectId } = useParams();
  const { data: project = {} } = useGetProjectDetailsQuery(projectId);
  const PROJECT_ANALYSES_URL = setBaseUrl(
    `/ajax/analyses/list?projectId=${projectId}`
  );

  return (
    <PagedTableProvider url={PROJECT_ANALYSES_URL}>
      <Title level={2}>{i18n("ProjectAnalysesPage.title")}</Title>
      <AnalysesTableProvider>
        <AnalysesTable canManage={project.canManage}/>
      </AnalysesTableProvider>
    </PagedTableProvider>
  );
};
