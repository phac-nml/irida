import React from "react";
import { render } from "react-dom";
import { navigate, Router, useLocation } from "@reach/router";
import { setBaseUrl } from "../../../utilities/url-utilities";
import ListProjectTemplates from "../../../components/metadata-template/list";
import Template from "../../../components/metadata-template/template";
import { Layout, PageHeader } from "antd";
import { grey1 } from "../../../styles/colors";
import CreateTemplate
  from "../../../components/metadata-template/create-template";

const { Content } = Layout;

/**
 * React component for setting up page routing on the project metadata template
 * page.
 * @returns {JSX.Element}
 * @constructor
 */
function ProjectMetadataTemplatesPage() {
  return (
    <Router>
      <PageLayout path={setBaseUrl("/projects/:projectId/metadata-templates")}>
        <ListProjectTemplates path="/" />
        <Template path={"/:templateId"} />
      </PageLayout>
    </Router>
  );
}

const PageLayout = ({ children, projectId }) => {
  const location = useLocation();

  /*
  Only show the back button if currently viewing a template.
  The url will end in a number if viewing a template
   */
  const onBack = location.pathname.match(/-templates$/)
    ? null
    : () => navigate(setBaseUrl(`/projects/${projectId}/metadata-templates`));

  return (
    <PageHeader
      className="site-page-header"
      title={i18n("ProjectMetadataTemplates.title")}
      onBack={onBack}
      extra={[
        onBack === null ? <CreateTemplate key="create-template" /> : null,
      ]}
    >
      <Layout>
        <Content style={{ backgroundColor: grey1 }}>{children}</Content>
      </Layout>
    </PageHeader>
  );
};

render(
  <ProjectMetadataTemplatesPage />,
  document.querySelector("#templates-root")
);
