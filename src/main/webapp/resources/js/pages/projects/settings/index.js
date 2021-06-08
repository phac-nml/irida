import { Redirect, Router } from "@reach/router";
import { Col, Layout, Row, Skeleton } from "antd";
import React, { Suspense } from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { useGetProjectDetailsQuery } from "../../../apis/projects/project";
import { getProjectRoles } from "../../../apis/projects/projects";
import { RolesProvider } from "../../../contexts/roles-context";
import { grey1 } from "../../../styles/colors";
import { SPACE_SM } from "../../../styles/spacing";
import { setBaseUrl } from "../../../utilities/url-utilities";
import SettingsNav from "./components/SettingsNav";
import store from "./store";

const ProjectDetails = React.lazy(() => import("./components/ProjectDetails"));
const ProjectProcessing = React.lazy(() =>
  import("./components/ProjectProcessing")
);
const ProjectMembers = React.lazy(() => import("./components/ProjectMembers"));
const ProjectGroups = React.lazy(() =>
  import("./components/ProjectUserGroups")
);

const MetadataLayout = React.lazy(() =>
  import("./components/metadata/MetadataLayout")
);

const MetadataFieldsListManager = React.lazy(() =>
  import("./components/metadata/MetadataFieldsListManager")
);

const MetadataFieldsListMember = React.lazy(() =>
  import("./components/metadata/MetadataFieldsListMember")
);

const MetadataTemplateManager = React.lazy(() =>
  import("./components/metadata/MetadataTemplateManager")
);
const MetadataTemplateMember = React.lazy(() =>
  import("./components/metadata/MetadataTemplateMember")
);
const MetadataTemplates = React.lazy(() =>
  import("./components/metadata/MetadataTemplates")
);

const AssociatedProjects = React.lazy(() =>
  import("./components/AssociatedProjects")
);

const ReferenceFiles = React.lazy(() => import("./components/ReferenceFiles"));

const ProjectSynchronizationSettings = React.lazy(() =>
  import("./components/ProjectSynchronizationSettings")
);

const DeleteProject = React.lazy(() => import("./components/DeleteProject"));

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

const { Content, Sider } = Layout;

/**
 * @file Base component for the project settings page.
 */

/**
 * This component is solely responsible for setting the url path for
 * the entire settings panel.
 * @returns {JSX.Element}
 * @constructor
 */
const SettingsLayout = () => (
  <Router>
    <ProjectSettings path={setBaseUrl("/projects/:projectId/settings/*")} />
  </Router>
);

/**
 * React component to handle the overall layout of the project settings page.
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
const ProjectSettings = (props) => {
  const { data: project = {} } = useGetProjectDetailsQuery(props.projectId);
  return (
    <Layout>
      <Sider width={200} style={{ backgroundColor: grey1 }}>
        <SettingsNav
          path={props["*"]}
          canManage={project.canManage}
          showRemote={project.canManage && project.remote}
        />
      </Sider>
      <Layout>
        <Content style={{ backgroundColor: grey1, paddingLeft: SPACE_SM }}>
          <Row>
            <Col lg={24} xxl={12}>
              <RolesProvider getRolesFn={getProjectRoles}>
                <Suspense fallback={<Skeleton />}>
                  <Router>
                    <ProjectDetails path="/details" />
                    <ProjectProcessing path="/processing" />
                    <ProjectMembers path="/members" />
                    <ProjectGroups path="/groups" />
                    <MetadataLayout path="/metadata">
                      <MetadataTemplates path="/templates" />
                      {project.canManage ? (
                        <>
                          <MetadataFieldsListManager path="/fields" />
                          <MetadataTemplateManager path="/templates/:id" />
                        </>
                      ) : (
                        <>
                          <MetadataFieldsListMember path="/fields" />
                          <MetadataTemplateMember path="/templates/:id" />
                        </>
                      )}
                    </MetadataLayout>
                    <AssociatedProjects path="/associated" />
                    <ReferenceFiles path="/references" />
                    <ProjectSynchronizationSettings path="/remote" />
                    {project.canManage && <DeleteProject path="/delete" />}
                    <Redirect from="/" to="/details" />
                  </Router>
                </Suspense>
              </RolesProvider>
            </Col>
          </Row>
        </Content>
      </Layout>
    </Layout>
  );
};

render(
  <Provider store={store}>
    <SettingsLayout />
  </Provider>,
  document.querySelector("#root")
);
