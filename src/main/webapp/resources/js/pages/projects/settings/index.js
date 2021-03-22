import React, { Suspense } from "react";
import { render } from "react-dom";
import { Router } from "@reach/router";
import { Layout, Skeleton } from "antd";
import { grey1 } from "../../../styles/colors";
import { SPACE_SM } from "../../../styles/spacing";
import { Provider, useDispatch } from "react-redux";
import SettingsNav from "./components/SettingsNav";
import store from "./store";
import { fetchProjectDetails } from "../redux/projectSlice";
const ProjectDetails = React.lazy(() => import("./components/ProjectDetails"));

const { Content, Sider } = Layout;

const SettingsLayout = () => (
  <Router>
    <ProjectSettings path="/projects/:projectId/settings/*" />
  </Router>
);

const ProjectSettings = (props) => {
  const dispatch = useDispatch();
  // const { remote } = useSelector((state) => state.project);
  //
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
