import React from "react";
import { render } from "react-dom";
import { Link, Router } from "@reach/router";
import { MetadataTemplatesList } from "./MetadataTemplatesList";
import { MetadataTemplate } from "./MetadataTemplate";
import { MetadataTemplates } from "./MetadataTemplates";
import { Col, Menu, Row, Space } from "antd";
import { MetadataFields } from "./MetadataFields";

const Content = ({ children, ...props }) => {
  const [selectedKey, setSelectedKey] = React.useState("fields");

  React.useEffect(() => {
    setSelectedKey(props["*"].includes("templates") ? "templates" : "fields");
  }, [props["*"]]);

  return (
    <Space style={{ display: "block" }}>
      <Menu mode="horizontal" selectedKeys={[selectedKey]}>
        <Menu.Item key="fields">
          <Link to="">Metadata Fields</Link>
        </Menu.Item>
        <Menu.Item key="templates">
          <Link to="templates">Metadata Templates</Link>
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
 * @returns {JSX.Element}
 * @constructor
 */
function ProjectMetadataTemplates() {
  return (
    <Router>
      <Content path={"/projects/:projectId/settings/metadata"}>
        <MetadataFields path="/" />
        <MetadataTemplates path="/templates">
          <MetadataTemplatesList path="/" />
          <MetadataTemplate path="/:id" />
        </MetadataTemplates>
      </Content>
    </Router>
  );
}

render(<ProjectMetadataTemplates />, document.querySelector("#templates-root"));
