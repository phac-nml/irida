import { Typography } from "antd";
import React from "react";
import { useParams } from "react-router-dom";
import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { ProjectUserGroupsTable } from "../../../../components/project-user-groups";
import { setBaseUrl } from "../../../../utilities/url-utilities";

const { Title } = Typography;

/**
 * React component to render the page to modify project user groups on a
 * specific project
 * @returns {*}
 * @constructor
 */
export default function ProjectUserGroups() {
  const { projectId } = useParams();
  return (
    <PagedTableProvider
      url={setBaseUrl(`/ajax/projects/groups?projectId=${projectId}`)}
    >
      <Title level={2}>{i18n("ProjectUserGroups.title")}</Title>
      <ProjectUserGroupsTable projectId={projectId} />
    </PagedTableProvider>
  );
}
