import React from "react";
import { Router } from "@reach/router";
import { ProjectMetadataTemplates } from "./ProjectMetadataTemplates";
import { Layout, PageHeader } from "antd";

export function ListMetadataTemplates() {
  const { href } = window.location;
  console.log(href);
  return (
    <PageHeader className="site-page-header" title={"METADATA TEMPLATES"}>
      <Layout>
        <Router>
          <ProjectMetadataTemplates path={href} />
        </Router>
      </Layout>
    </PageHeader>
  );
}
