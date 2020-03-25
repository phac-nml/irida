import React from "react";
import { render } from "react-dom";
import { PageHeader } from "antd";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { AddNewButton } from "../../components/Buttons/AddNewButton";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectUsersTable } from "../../components/Tables/ProjectUsersTable";

function ProjectUsersPage() {
  return (
    <>
      <PageHeader
        title={i18n("project.settings.page.title.members")}
        extra={<AddNewButton text={i18n("project.members.edit.add")} />}
      />
      <ProjectUsersTable />
    </>
  );
}

function ProjectUsersApp() {
  return (
    <PagedTableProvider
      url={setBaseUrl(`/ajax/projects/${window.project.id}/settings/members`)}
    >
      <ProjectUsersPage />
    </PagedTableProvider>
  );
}

render(<ProjectUsersApp />, document.querySelector("#users-root"));
