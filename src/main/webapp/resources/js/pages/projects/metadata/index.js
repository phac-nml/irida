import React from "react";
import { render } from "react-dom";
import { Link, Router } from "@reach/router";
import { MetadataTemplatesList } from "./MetadataTemplatesList";
import { MetadataTemplateManager } from "./MetadataTemplateManager";
import { MetadataTemplates } from "./MetadataTemplates";
import { Col, Layout, Menu, Row } from "antd";
import { MetadataFieldsList } from "./MetadataFieldsList";

import store from "./store";
import { Provider, useDispatch, useSelector } from "react-redux";
import { fetchFieldsForProject } from "../redux/fieldsSlice";
import { fetchTemplatesForProject } from "../redux/templatesSlice";
import { MetadataFields } from "./MetadataFields";
import { MetadataTemplateMember } from "./MetadataTemplateMember";
import { grey1 } from "../../../styles/colors";
import { SPACE_SM } from "../../../styles/spacing";
import { fetchProjectDetails } from "../redux/projectSlice";

const { Content, Sider } = Layout;

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
    dispatch(fetchProjectDetails(projectId));
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
    <Layout>
      <Sider width={200} style={{ backgroundColor: grey1 }}>
        <Menu mode="inline" selectedKeys={[selectedKey]}>
          <Menu.Item key="fields">
            <Link className="t-m-field-link" to="fields">
              {i18n("MetadataFields.title")}
            </Link>
          </Menu.Item>
          <Menu.Item key="templates">
            <Link className="t-m-template-link" to="templates">
              {i18n("ProjectMetadataTemplates.title")}
            </Link>
          </Menu.Item>
        </Menu>
      </Sider>
      <Layout>
        <Content style={{ backgroundColor: grey1, paddingLeft: SPACE_SM }}>
          {children}
        </Content>
      </Layout>
    </Layout>
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
      <MetadataLayout path={"/projects/:projectId/metadata"}>
        <MetadataFields path="/fields">
          <MetadataFieldsList path="/" />
        </MetadataFields>
        <MetadataTemplates path="/templates">
          <MetadataTemplatesList path="/" />
          {canManage ? (
            <MetadataTemplateManager path="/:id" />
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
  document.querySelector("#root")
);
