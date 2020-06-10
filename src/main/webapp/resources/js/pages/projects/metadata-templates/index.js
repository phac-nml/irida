import React from "react";
import { render } from "react-dom";
import { ListMetadataTemplates } from "../../../components/metadata-template/list";

/**
 * Display a list of metadata templates that are associated with a project.
 * @returns {JSX.Element}
 * @constructor
 */
function ProjectMetadataTemplates() {
  return <ListMetadataTemplates />;
}

render(<ProjectMetadataTemplates />, document.querySelector("#templates-root"));
