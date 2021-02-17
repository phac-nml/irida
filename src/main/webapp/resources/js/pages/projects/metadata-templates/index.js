import React from "react";
import { render } from "react-dom";
import { Button, PageHeader, Typography } from "antd";
import { Router } from "@reach/router";
import { MetadataTemplatesList } from "./MetadataTemplatesList";

const { Title } = Typography;

/**
 * Display a list of metadata templates that are associated with a project.
 * @returns {JSX.Element}
 * @constructor
 */
function ProjectMetadataTemplates() {
  return (
    <PageHeader
      title={i18n("ProjectMetadataTemplates.title")}
      extra={[
        <Button key="create" className="t-create-template-btn">
          Create New Template
        </Button>,
      ]}
    >
      <Router>
        <MetadataTemplatesList
          path={"/projects/5/settings/metadata-templates/"}
        />
      </Router>
    </PageHeader>
  );
}

render(<ProjectMetadataTemplates />, document.querySelector("#templates-root"));
