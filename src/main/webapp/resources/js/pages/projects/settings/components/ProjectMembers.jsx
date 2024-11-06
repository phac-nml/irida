import { Typography } from "antd";
import React from "react";
import { useParams } from "react-router-dom";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { ProjectMembersTable } from "../../../../components/project-members";
import { setBaseUrl } from "../../../../utilities/url-utilities";

const { Title } = Typography;

/**
 * React component that displays the Project > Members page.
 * @returns {*}
 * @constructor
 */
export default function ProjectMembersPage() {
  const { projectId } = useParams();
  return (
    <PagedTableProvider
      url={setBaseUrl(`/ajax/projects/members?projectId=${projectId}`)}
    >
      <>
        <Title level={2}>{i18n("project.settings.page.title.members")}</Title>
        <ProjectMembersTable projectId={projectId} />
      </>
    </PagedTableProvider>
  );
}
