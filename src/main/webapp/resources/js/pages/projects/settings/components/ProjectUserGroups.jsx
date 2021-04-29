import { Typography } from "antd";
import React from "react";
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
export default function ProjectUserGroups(props) {
  return (
    <PagedTableProvider
      url={setBaseUrl(`/ajax/projects/groups?projectId=${props.projectId}`)}
    >
      <Title level={2}>{i18n("ProjectUserGroups.title")}</Title>
      <ProjectUserGroupsTable />
    </PagedTableProvider>
  );
}
