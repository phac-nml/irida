import React from "react";
import { render } from "react-dom";
import { Link, Router } from "@reach/router";
import { MetadataTemplatesList } from "./MetadataTemplatesList";
import { MetadataTemplateAdmin } from "./MetadataTemplateAdmin";
import { MetadataTemplates } from "./MetadataTemplates";
import { Col, Menu, Row } from "antd";
import { MetadataFieldsList } from "./MetadataFieldsList";

import store from "./store";
import { Provider, useDispatch, useSelector } from "react-redux";
import { fetchFieldsForProject } from "../redux/fieldsSlice";
import { fetchTemplatesForProject } from "../redux/templatesSlice";
import { MetadataFields } from "./MetadataFields";
import { MetadataTemplateMember } from "./MetadataTemplateMember";

/**
 * React component handles the layout of the metadata fields and templates page.
 *
 * @param {JSX.Element} children - Components making up the metadata page
 * @param {Object} props - all remaining props including those passed by Reach Router
 * @returns {JSX.Element}
 * @constructor
 */
const MetadataLayout = ({ projectId, children, ...props }) => {
  const dispatch = useDispatch();
  /**
   * @type {[String, Function]} which page is currently being displayed
   */
  const [selectedKey, setSelectedKey] = React.useState("fields");

  React.useEffect(() => {
    /*
    Fetch all fields and templates.
     */
    dispatch(fetchFieldsForProject(projectId));
    dispatch(fetchTemplatesForProject(projectId));
  }, []);

  React.useEffect(() => {
    /*
    Determine which menu item is currently active
     */
    setSelectedKey(props["*"].includes("templates") ? "templates" : "fields");
  }, [props["*"]]);

  return (
    <Row>
      <Col xs={24} lg={18} xxl={12}>
        <Menu mode="horizontal" selectedKeys={[selectedKey]}>
          <Menu.Item key="fields">
            <Link to="fields">{i18n("MetadataFields.title")}</Link>
          </Menu.Item>
          <Menu.Item key="templates">
            <Link to="templates">{i18n("ProjectMetadataTemplates.title")}</Link>
          </Menu.Item>
        </Menu>
        {children}
      </Col>
    </Row>
  );
};

/**
 * Display a list of metadata templates that are associated with a project.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ProjectMetadata() {
  const { canManage } = useSelector((state) => state.project);

  return (
    <Router>
      <MetadataLayout path={"/projects/:projectId/settings/metadata"}>
        <MetadataFields path="/fields">
          <MetadataFieldsList path="/" />
        </MetadataFields>
        <MetadataTemplates path="/templates">
          <MetadataTemplatesList path="/" />
          {canManage ? (
            <MetadataTemplateAdmin path="/:id" />
          ) : (
            <MetadataTemplateMember path="/:id" />
          )}
        </MetadataTemplates>
      </MetadataLayout>
    </Router>
  );
}

render(
  <Provider store={store}>
    <ProjectMetadata />
  </Provider>,
  document.querySelector("#templates-root")
);
