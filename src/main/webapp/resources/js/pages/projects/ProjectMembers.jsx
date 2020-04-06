import React, { useEffect, useRef } from "react";
import { render } from "react-dom";
import { Router, Link, useNavigate, useLocation } from "@reach/router";
import { Layout, PageHeader } from "antd";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { AddNewButton } from "../../components/Buttons/AddNewButton";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ProjectMembersTable } from "../../components/Tables/ProjectMembersTable";
import { IconMembers } from "../../components/icons/Icons";

const { Content } = Layout;

function ProjectMembersPage({ children }) {
  const navigate = useNavigate();
  const location = useLocation();

  const inEdit = location.pathname.endsWith("edit");

  function returnToMembers() {
    navigate(
      `${setBaseUrl(`projects/${window.project.id}/settings/members`)}`,
      {
        replace: true
      }
    );
  }

  return (
    <PageHeader
      avatar={{ icon: <IconMembers /> }}
      onBack={inEdit ? returnToMembers : null}
      title={i18n("project.settings.page.title.members")}
      extra={
        inEdit ? null : (
          <Link to={`${window.location.pathname}/edit`}>
            <AddNewButton text={i18n("project.members.edit.add")} />
          </Link>
        )
      }
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

function EditUsers() {
  return <h1>EDIT USERS</h1>;
}

render(
  <Router>
    <ProjectMembersPage
      path={setBaseUrl(`projects/${window.project.id}/settings/members`)}
    >
      <WrappedMembersTable path={"/"} />
      <EditUsers path="edit" />
    </ProjectMembersPage>
  </Router>,
  document.querySelector("#users-root")
);
