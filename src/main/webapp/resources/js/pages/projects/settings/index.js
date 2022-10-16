import { Col, Layout, Row, Spin } from "antd";
import React, { Suspense, useState } from "react";
import { createRoot } from "react-dom/client";
import { Provider } from "react-redux";
import {
  BrowserRouter,
  Outlet,
  Route,
  Routes,
  useParams,
} from "react-router-dom";
import { useGetProjectDetailsQuery } from "../../../apis/projects/project";
import { MetadataRolesProvider } from "../../../contexts/metadata-roles-context";
import { ProjectRolesProvider } from "../../../contexts/project-roles-context";
import { grey1 } from "../../../styles/colors";
import { SPACE_SM } from "../../../styles/spacing";
import { setBaseUrl } from "../../../utilities/url-utilities";
import SettingsNav from "./components/SettingsNav";
import store from "./store";
import { getProjectRoles } from "../../../apis/projects/projects";
import { useMetadataRoles } from "../../../contexts/metadata-roles-context";

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

const MetadataFields = React.lazy(() =>
  import("./components/metadata/MetadataFields")
);

const MetadataTemplates = React.lazy(() =>
  import("./components/metadata/MetadataTemplates")
);

const MetadataTemplate = React.lazy(() =>
  import("./components/metadata/MetadataTemplate")
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
  <Routes>
    <Route
      path={setBaseUrl("/projects/:projectId/settings")}
      element={<ProjectSettings />}
    >
      <Route index element={<ProjectDetails />} />
      <Route path="processing" element={<ProjectProcessing />} />
      <Route path="members" element={<ProjectMembers />} />
      <Route path="groups" element={<ProjectGroups />} />
      <Route path="metadata" element={<MetadataLayout />}>
        <Route path="fields" element={<MetadataFields />} />
        <Route path="templates" element={<MetadataTemplates />} />
        <Route path="templates/:id" element={<MetadataTemplate />} />
      </Route>
      <Route path="associated" element={<AssociatedProjects />} />
      <Route path="references" element={<ReferenceFiles />} />
      <Route path="remote" element={<ProjectSynchronizationSettings />} />
      <Route path="delete" element={<DeleteProject />} />
      <Route path="*" element={<ProjectDetails />} />
    </Route>
  </Routes>
);

/**
 * React component to handle the overall layout of the project settings page.
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
const ProjectSettings = () => {
  const { projectId } = useParams();

  const { data: project = {} } = useGetProjectDetailsQuery(projectId, {
    skip: !projectId,
  });

  const [basePath] = useState(() =>
    setBaseUrl(`/projects/${projectId}/settings/`)
  );

  return (
    <Layout>
      <Sider width={200} style={{ backgroundColor: grey1 }}>
        <SettingsNav
          basePath={basePath}
          canManage={project.canManage}
          showRemote={project.canManage && project.remote}
        />
      </Sider>
      <Layout>
        <Content style={{ backgroundColor: grey1, paddingLeft: SPACE_SM }}>
          <Row>
            <Col lg={24} xxl={12}>
              <ProjectRolesProvider getRolesFn={getProjectRoles}>
                <MetadataRolesProvider getRolesFn={useMetadataRoles}>
                  <Suspense fallback={<Spin />}>
                    <Outlet />
                  </Suspense>
                </MetadataRolesProvider>
              </ProjectRolesProvider>
            </Col>
          </Row>
        </Content>
      </Layout>
    </Layout>
  );
};

const root = createRoot(document.querySelector("#root"));
root.render(
  <BrowserRouter>
    <Provider store={store}>
      <SettingsLayout />
    </Provider>
  </BrowserRouter>
);
