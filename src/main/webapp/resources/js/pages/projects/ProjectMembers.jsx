import React from "react";
import { render } from "react-dom";
import { Typography } from "antd";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectMembersTable } from "../../components/project-members";
import { RolesProvider } from "../../contexts/roles-context";
import { getProjectRoles } from "../../apis/projects/projects";

const { Title } = Typography;

/**
 * React component that displays the Project > Members page.
 * @returns {*}
 * @constructor
 */
function ProjectMembersPage() {
  return (
    <PagedTableProvider
      url={setBaseUrl(`/ajax/projects/${window.project.id}/members`)}
    >
      <RolesProvider rolesFn={getProjectRoles}>
        <>
          <Title level={2}>{i18n("project.settings.page.title.members")}</Title>
          <ProjectMembersTable />
        </>
      </RolesProvider>
    </PagedTableProvider>
  );
}

render(<ProjectMembersPage />, document.querySelector("#users-root"));
