import React from "react";
import { render } from "react-dom";
import { Layout, PageHeader } from "antd";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectMembersTable } from "../../components/project-members";
import { IconMembers } from "../../components/icons/Icons";
import { ProjectRolesProvider } from "../../contexts/ProjectRolesContext";

const { Content } = Layout;

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
        <PageHeader
          avatar={{ icon: <IconMembers /> }}
          title={i18n("project.settings.page.title.members")}
        >
          <Content>
            <ProjectMembersTable />
          </Content>
        </PageHeader>
      </ProjectRolesProvider>
    </PagedTableProvider>
  );
}

render(<ProjectMembersPage />, document.querySelector("#users-root"));
