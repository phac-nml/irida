import { Redirect, Router } from "@reach/router";
import { Col, Layout, Row, Skeleton } from "antd";
import React, { Suspense } from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { useGetProjectDetailsQuery } from "../../../apis/projects/project";
import { grey1 } from "../../../styles/colors";
import { SPACE_SM } from "../../../styles/spacing";
import { setBaseUrl } from "../../../utilities/url-utilities";

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
 * This component is solely responsible for setting the url path for
 * the entire analyses panel.
 * @returns {JSX.Element}
 * @constructor
 */
const AnalysesLayout = () => (
  <Router>
    <ProjectAnalyses path={setBaseUrl("/projects/:projectId/analyses/*")} />
  </Router>
);

/**
 * React component to handle the overall layout of the project analyses page.
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
const ProjectAnalyses = (props) => {
  const { data: project = {} } = useGetProjectDetailsQuery(props.projectId);
  return (
    <Layout>
      <Content style={{ backgroundColor: grey1, paddingLeft: SPACE_SM }}>
        <Row>
          <Col lg={24} xxl={12}>
            <Suspense fallback={<Skeleton />}>
              <Router>
                <ProjectAnalysesPage
                  path="/project-analyses"
                  canManage={project.canManage}
                />
                <SharedSingleSampleAnalysisOutputs path="/shared-outputs" />
                <AutomatedSingleSampleAnalysisOutputs path="/automated-outputs" />
                <Redirect from="/" to="/project-analyses" />
              </Router>
            </Suspense>
          </Col>
        </Row>
      </Content>
    </Layout>
  );
};

render(
  <Provider store={store}>
    <AnalysesLayout />
  </Provider>,
  document.querySelector("#root")
);
