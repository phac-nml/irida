import React from "react";
import { render } from "react-dom";
import { ProjectRolesProvider } from "../../contexts/ProjectRolesContext";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectGroupsTable } from "../../components/project-groups/ProjectGroupsTable";

function ProjectGroupsPage() {
  return (
    <PagedTableProvider
      url={setBaseUrl(`/ajax/projects/${window.project.id}/groups`)}
    >
      <ProjectRolesProvider>
        <ProjectGroupsTable />
      </ProjectRolesProvider>
    </PagedTableProvider>
  );
}

render(<ProjectGroupsPage />, document.querySelector("#settings-root"));
