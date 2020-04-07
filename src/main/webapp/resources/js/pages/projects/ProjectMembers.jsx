import React from "react";
import { render } from "react-dom";
import { Layout, PageHeader } from "antd";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { AddNewButton } from "../../components/Buttons/AddNewButton";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectMembersTable } from "../../components/project-members";
import { IconMembers } from "../../components/icons/Icons";
import { AddMembersButton } from "../../components/project-members/AddMemberButton";

const { Content } = Layout;

function ProjectMembersPage({ children }) {
  return (
    <PageHeader
      avatar={{ icon: <IconMembers /> }}
      title={i18n("project.settings.page.title.members")}
      extra={<AddMembersButton />}
    >
      <Content>{children}</Content>
    </PageHeader>
  );
}

function WrappedMembersTable() {
  return (
    <PagedTableProvider
      url={setBaseUrl(`/ajax/projects/${window.project.id}/members`)}
    >
      <ProjectMembersTable />
    </PagedTableProvider>
  );
}

render(
  <ProjectMembersPage
    path={setBaseUrl(`projects/${window.project.id}/settings/members`)}
  >
    <WrappedMembersTable />
  </ProjectMembersPage>,
  document.querySelector("#users-root")
);
