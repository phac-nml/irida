import React from "react";
import {render} from "react-dom";
import {Link, Router} from "@reach/router";
import {MetadataTemplatesList} from "./MetadataTemplatesList";
import {MetadataTemplate} from "./MetadataTemplate";
import {MetadataTemplates} from "./MetadataTemplates";
import {Col, Menu, Row, Space} from "antd";
import {MetadataFields} from "./MetadataFields";

/**
 * React component handles the layout of the metadata fields and templates page.
 *
 * @param {JSX.Element} children - Components making up the metadata page
 * @param {Object} props - all remaining props including those passed by Reach Router
 * @returns {JSX.Element}
 * @constructor
 */
const Content = ({ children, ...props }) => {
  /**
   * @type {[String, Function]} which page is currently being displayed
   */
  const [selectedKey, setSelectedKey] = React.useState("fields");

  React.useEffect(() => {
    setSelectedKey(props["*"].includes("templates") ? "templates" : "fields");
  }, [props["*"]]);

  return (
    <Space style={{ display: "block" }}>
      <Menu mode="horizontal" selectedKeys={[selectedKey]}>
        <Menu.Item key="fields">
          <Link to="fields">{i18n("MetadataFields.title")}</Link>
        </Menu.Item>
        <Menu.Item key="templates">
          <Link to="templates">{i18n("ProjectMetadataTemplates.title")}</Link>
        </Menu.Item>
      </Menu>
      <Row>
        <Col xs={24} lg={18} xxl={12}>
          {children}
        </Col>
      </Row>
    </Space>
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
    <Router>
      <Content path={"/projects/:projectId/settings/metadata"}>
        <MetadataFields path="/fields/" />
        <MetadataTemplates path="/templates">
          <MetadataTemplatesList path="/" />
          <MetadataTemplate path="/:id" />
        </MetadataTemplates>
      </Content>
    </Router>
  );
}

render(<ProjectMetadataTemplates />, document.querySelector("#templates-root"));
