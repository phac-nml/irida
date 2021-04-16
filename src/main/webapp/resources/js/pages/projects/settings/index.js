import { Redirect, Router } from "@reach/router";
import { Col, Layout, Row, Skeleton } from "antd";
import React, { Suspense } from "react";
import { render } from "react-dom";
import { Provider, useDispatch } from "react-redux";
import { grey1 } from "../../../styles/colors";
import { SPACE_SM } from "../../../styles/spacing";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { fetchProjectDetails } from "../redux/projectSlice";
import SettingsNav from "./components/SettingsNav";
import store from "./store";

const ProjectDetails = React.lazy(() => import("./components/ProjectDetails"));
const ProjectProcessing = React.lazy(() =>
  import("./components/ProjectProcessing")
);
const ProjectMembers = React.lazy(() => import("./components/ProjectMembers"));

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
  const dispatch = useDispatch();

  React.useEffect(() => {
    dispatch(fetchProjectDetails(props.projectId));
  }, []);

  return (
    <Layout>
      <Sider width={200} style={{ backgroundColor: grey1 }}>
        <SettingsNav path={props["*"]} />
      </Sider>
      <Layout>
        <Content style={{ backgroundColor: grey1, paddingLeft: SPACE_SM }}>
          <Row>
            <Col lg={24} xxl={12}>
              <Suspense fallback={<Skeleton />}>
                <Router>
                  <ProjectDetails path="/details" />
                  <ProjectProcessing path="/processing" />
                  <ProjectMembers path="/members" />
                  <Redirect from="/" to="/details" />
                </Router>
              </Suspense>
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
