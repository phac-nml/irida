import { Typography } from "antd";
import React from "react";
import { useSelector } from "react-redux";
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
  const { id: projectId } = useSelector((state) => state.project);

  return (
    <PagedTableProvider
      url={setBaseUrl(`/ajax/projects/${projectId}/user-groups`)}
    >
      <Title level={2}>{i18n("ProjectUserGroups.title")}</Title>
      <ProjectUserGroupsTable />
    </PagedTableProvider>
  );
}
