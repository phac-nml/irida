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
} from "react-router-dom";
import { useGetPotentialProjectsToShareToQuery } from "../../../apis/projects/projects";
import { useGetSampleIdsForProjectQuery } from "../../../apis/projects/samples";
import { ShareSamples } from "./ShareSamples";
import { setProject } from "./shareSlice";
import store from "./store";

/**
 * Base component for sharing samples between projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ShareLayout() {
  const dispatch = useDispatch();
  const history = useHistory();
  const [samples, setSamples] = React.useState();
  const [options, setOptions] = React.useState();
  const { originalSamples, currentProject, projectId } = useSelector(
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
    setSamples(originalSamples);
  }, [originalSamples]);

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
      history.push("/samples");
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
            <Menu mode="horizontal">
              <Menu.Item key="samples">
                <Link to="/samples">Samples</Link>
              </Menu.Item>
            </Menu>
            <Switch>
              <Route path="/samples">
                <ShareSamples sampleIds={sampleIds} />
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
