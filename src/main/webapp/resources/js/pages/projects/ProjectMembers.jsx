import React from "react";
import { render } from "react-dom";
import { Typography } from "antd";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectMembersTable } from "../../components/project-members";
import { ProjectRolesProvider } from "../../contexts/ProjectRolesContext";

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
      <ProjectRolesProvider>
        <>
          <Title level={2}>{i18n("project.settings.page.title.members")}</Title>
          <ProjectMembersTable />
        </>
      </ProjectRolesProvider>
    </PagedTableProvider>
  );
}

render(<ProjectMembersPage />, document.querySelector("#users-root"));
