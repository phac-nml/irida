import { Form, Menu, Select, Space, Typography } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider, useDispatch, useSelector } from "react-redux";
import {
  BrowserRouter as Router,
  Link,
  Route,
  Switch,
  useHistory,
  useLocation,
} from "react-router-dom";
import { useGetPotentialProjectsToShareToQuery } from "../../../apis/projects/projects";
import { useGetSampleIdsForProjectQuery } from "../../../apis/projects/samples";
import { ShareSamples } from "./ShareSamples";
import { setProject } from "./shareSlice";
import store from "./store";

/**
IGNORE THIS WILL BE MOVED / ENSURING ROUTER WORKING
 */
const ShareMetadata = () => <div>METAsDATA</div>;

/**
 * Base component for sharing samples between projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ShareLayout() {
  const dispatch = useDispatch();
  const history = useHistory();
  const location = useLocation();
  const [options, setOptions] = React.useState();
  const { currentProject, projectId } = useSelector(
    (state) => state.shareReducer
  );

  const {
    data: projects,
    isLoading: projectLoading,
  } = useGetPotentialProjectsToShareToQuery(currentProject, {
    skip: !currentProject,
  });

  const { data: sampleIds } = useGetSampleIdsForProjectQuery(projectId, {
    skip: !projectId,
  });

  React.useEffect(() => {
    if (!projectLoading) {
      setOptions(
        projects.map((project) => ({
          label: project.name,
          value: project.identifier,
        }))
      );
    }
  }, [projects, projectLoading]);

  React.useEffect(() => {
    if (projectId) {
      // Go back to samples whenever there is a new project selected
      history.push("/");
    }
  }, [history, projectId]);

  const updateCurrentSampleIds = (projectId) => dispatch(setProject(projectId));

  return (
    <Form layout="vertical">
      <Space direction="vertical" style={{ display: "block" }} size="large">
        <Typography.Title level={4}>
          {i18n("ShareSamples.title")}
        </Typography.Title>
        <Form.Item label={i18n("ShareSamples.projects")}>
          <Select
            size="large"
            style={{ width: `100%` }}
            loading={projectLoading}
            options={options}
            onChange={updateCurrentSampleIds}
          />
        </Form.Item>
        {sampleIds && (
          <Space direction="vertical" style={{ display: "block" }}>
            <Menu mode="horizontal" selectedKeys={[location.pathname]}>
              <Menu.Item key="/">
                <Link to="/">Samples</Link>
              </Menu.Item>
              <Menu.Item key="/metadata">
                <Link to="/metadata">Metadata</Link>
              </Menu.Item>
            </Menu>
            <Switch>
              <Route exact path="/">
                <ShareSamples sampleIds={sampleIds} />
              </Route>
              <Route path="/metadata">
                <ShareMetadata />
              </Route>
            </Switch>
          </Space>
        )}
      </Space>
    </Form>
  );
}

render(
  <Router basename={window.location.pathname}>
    <Provider store={store}>
      <ShareLayout />
    </Provider>
  </Router>,
  document.querySelector("#root")
);
