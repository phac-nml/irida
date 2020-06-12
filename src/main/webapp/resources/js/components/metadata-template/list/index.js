import React from "react";
import { navigate, Router, useLocation } from "@reach/router";
import { ProjectMetadataTemplates } from "./ProjectMetadataTemplates";
import { Button, Layout, PageHeader } from "antd";
import { grey1 } from "../../../styles/colors";
import { TemplatePage } from "../details/TemplatePage";

const { Content } = Layout;

export function ListMetadataTemplates() {
  return (
    <Router>
      <Test path={"/projects/:projectId/metadata-templates"}>
        <ProjectMetadataTemplates path="/" />
        <TemplatePage path={"/:templateId"} />
      </Test>
    </Router>
  );
}

const Test = ({ children, projectId }) => {
  const location = useLocation();
  const onBack = location.pathname.match(/-templates$/)
    ? null
    : () => navigate(`/projects/${projectId}/metadata-templates`);

  return (
    <PageHeader
      className="site-page-header"
      title={i18n("ProjectMetadataTemplates.title")}
      onBack={onBack}
      extra={[
        <Button key="create-btn" className="t-create-template-btn">
          Create New Template
        </Button>,
      ]}
    >
      <Layout>
        <Content style={{ backgroundColor: grey1 }}>{children}</Content>
      </Layout>
    </PageHeader>
  );
};
