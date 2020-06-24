import React from "react";
import { render } from "react-dom";
import { ProjectUserGroupsTable } from "../../components/project-user-groups";
import { Typography } from "antd";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import { RolesProvider } from "../../contexts";
import { getProjectRoles } from "../../apis/projects/projects";

const { Title } = Typography;

/**
 * React component to render the page to modify project user groups on a
 * specific project
 * @returns {*}
 * @constructor
 */
function ProjectUserGroups() {
  return (
    <PagedTableProvider
      url={setBaseUrl(`/ajax/projects/${window.project.id}/user-groups`)}
    >
      <RolesProvider rolesFn={getProjectRoles}>
        <Title level={2}>{i18n("ProjectUserGroups.title")}</Title>
        <ProjectUserGroupsTable />
      </RolesProvider>
    </PagedTableProvider>
  );
}

render(<ProjectUserGroups />, document.querySelector("#groups-root"));
