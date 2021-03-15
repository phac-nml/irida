import React from "react";
import { render } from "react-dom";
import { Link, Router } from "@reach/router";
import { MetadataTemplatesList } from "./MetadataTemplatesList";
import { MetadataTemplate } from "./MetadataTemplate";
import { MetadataTemplates } from "./MetadataTemplates";
import { Col, Menu, Row, Space } from "antd";
import { MetadataFields } from "./MetadataFields";

import store from "./redux/store";
import { Provider, useDispatch } from "react-redux";
import { fetchFieldsForProject } from "./redux/fields/fieldsSlice";
import { fetchTemplatesForProject } from "./redux/templates/templatesSlice";

/**
 * React component handles the layout of the metadata fields and templates page.
 *
 * @param {JSX.Element} children - Components making up the metadata page
 * @param {Object} props - all remaining props including those passed by Reach Router
 * @returns {JSX.Element}
 * @constructor
 */
const Content = ({ projectId, children, ...props }) => {
  const dispatch = useDispatch();
  /**
   * @type {[String, Function]} which page is currently being displayed
   */
  const [selectedKey, setSelectedKey] = React.useState("fields");

  React.useEffect(() => {
    dispatch(fetchFieldsForProject(projectId));
    dispatch(fetchTemplatesForProject(projectId));
  }, []);

  React.useEffect(() => {
    setSelectedKey(props["*"].includes("templates") ? "templates" : "fields");
  }, [props["*"]]);

  return (
    <Row>
      <Col xs={24} lg={18} xxl={12}>
        <Space direction="vertical" size="large" style={{ display: "block" }}>
          <Menu mode="horizontal" selectedKeys={[selectedKey]}>
            <Menu.Item key="fields">
              <Link to="fields">{i18n("MetadataFields.title")}</Link>
            </Menu.Item>
            <Menu.Item key="templates">
              <Link to="templates">
                {i18n("ProjectMetadataTemplates.title")}
              </Link>
            </Menu.Item>
          </Menu>
          {children}
        </Space>
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
function ProjectMetadataTemplates() {
  return (
    <Provider store={store}>
      <Router>
        <Content path={"/projects/:projectId/settings/metadata"}>
          <MetadataFields path="/fields/" />
          <MetadataTemplates path="/templates">
            <MetadataTemplatesList path="/" />
            <MetadataTemplate path="/:id" />
          </MetadataTemplates>
        </Content>
      </Router>
    </Provider>
  );
}

render(<ProjectMetadataTemplates />, document.querySelector("#templates-root"));
