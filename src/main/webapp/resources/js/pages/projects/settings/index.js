import React, { Suspense } from "react";
import { render } from "react-dom";
import { Router, Redirect } from "@reach/router";
import { Layout, Skeleton } from "antd";
import { grey1 } from "../../../styles/colors";
import { SPACE_SM } from "../../../styles/spacing";
import { Provider, useDispatch } from "react-redux";
import { setBaseUrl } from "../../../utilities/url-utilities";
import SettingsNav from "./components/SettingsNav";
import store from "./store";
import { fetchProjectDetails } from "../redux/projectSlice";
const ProjectDetails = React.lazy(() => import("./components/ProjectDetails"));
const ProjectProcessing = React.lazy(() =>
  import("./components/ProjectProcessing")
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
          <Suspense fallback={<Skeleton />}>
            <Router>
              <ProjectDetails path="/details" />
              <ProjectProcessing path="/processing" />
              <Redirect from="/" to="/details" />
            </Router>
          </Suspense>
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
