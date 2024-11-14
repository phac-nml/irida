import { Col, Layout, Row, Skeleton } from "antd";
import React, { Suspense } from "react";
import { createRoot } from 'react-dom/client';
import { Provider } from "react-redux";
import {
  BrowserRouter,
  Outlet,
  Route,
  Routes,
  useParams,
} from "react-router-dom";
import { grey1 } from "../../../styles/colors";
import { SPACE_SM } from "../../../styles/spacing";
import { setBaseUrl } from "../../../utilities/url-utilities";
import AnalysesNav from "./components/AnalysesNav";

import store from "./store";

const ProjectAnalysesPage = React.lazy(() =>
  import("./components/ProjectAnalysesPage")
);

const SharedSingleSampleAnalysisOutputs = React.lazy(() =>
  import("./components/SharedSingleSampleAnalysisOutputs")
);

const AutomatedSingleSampleAnalysisOutputs = React.lazy(() =>
  import("./components/AutomatedSingleSampleAnalysisOutputs")
);

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

const { Content, Sider } = Layout;

/**
 * @file Base component for the project analyses page.
 */

/**
 * React component to handle the overall layout of the project analyses page.
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
function ProjectAnalyses() {
  const props = useParams();

  return (
    <Layout>
      <Layout>
        <Sider width={310} style={{ backgroundColor: grey1 }}>
          <AnalysesNav path={props["*"]} />
        </Sider>

        <Content style={{ backgroundColor: grey1, paddingLeft: SPACE_SM }}>
          <Row>
            <Col lg={24} xxl={24}>
              <Suspense fallback={<Skeleton />}>
                <Outlet />
              </Suspense>
            </Col>
          </Row>
        </Content>
      </Layout>
    </Layout>
  );
}

const container = document.getElementById('root');
const root = createRoot(container);
root.render(
  <BrowserRouter>
    <Provider store={store}>
      <Routes>
        <Route
          path={setBaseUrl("/projects/:projectId/analyses/*")}
          element={<ProjectAnalyses />}
        >
          <Route index element={<ProjectAnalysesPage />} />
          <Route path="project-analyses" element={<ProjectAnalysesPage />} />
          <Route
            path="shared-outputs"
            element={<SharedSingleSampleAnalysisOutputs />}
          />
          <Route
            path="automated-outputs"
            element={<AutomatedSingleSampleAnalysisOutputs />}
          />
          <Route path="*" element={<ProjectAnalysesPage />} />
        </Route>
      </Routes>
    </Provider>
  </BrowserRouter>
);
