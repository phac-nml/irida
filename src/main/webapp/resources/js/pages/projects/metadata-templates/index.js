import React from "react";
import { render } from "react-dom";
import { Router } from "@reach/router";
import { MetadataTemplatesList } from "./MetadataTemplatesList";
import { MetadataTemplate } from "./MetadataTemplate";

const Content = ({ children }) => children;

/**
 * Display a list of metadata templates that are associated with a project.
 * @returns {JSX.Element}
 * @constructor
 */
function ProjectMetadataTemplates() {
  return (
    <Router>
      <Content path={"/projects/5/settings/metadata-templates"}>
        <MetadataTemplatesList path="/" />
        <MetadataTemplate path="/:id" />
      </Content>
    </Router>
  );
}

render(<ProjectMetadataTemplates />, document.querySelector("#templates-root"));
