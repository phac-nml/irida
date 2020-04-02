import React from "react";
import { render } from "react-dom";
import { Layout, PageHeader } from "antd";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { AddNewButton } from "../../components/Buttons/AddNewButton";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectMembersTable } from "../../components/Tables/ProjectMembersTable";
import { IconMembers } from "../../components/icons/Icons";

const { Content } = Layout;

function ProjectUsersPage() {
  return (
    <PageHeader
      avatar={{ icon: <IconMembers /> }}
      title={i18n("project.settings.page.title.members")}
      extra={<AddNewButton text={i18n("project.members.edit.add")} />}
    >
      <Content>
        <ProjectMembersTable />
      </Content>
    </PageHeader>
  );
}

function ProjectUsersApp() {
  return (
    <PagedTableProvider
      url={setBaseUrl(`/ajax/projects/${window.project.id}/members`)}
    >
      <ProjectUsersPage />
    </PagedTableProvider>
  );
}

render(<ProjectUsersApp />, document.querySelector("#users-root"));
