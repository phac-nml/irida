import { Router } from "@reach/router";
import { configureStore } from "@reduxjs/toolkit";
import { Typography } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import {
  projectApi,
  useGetProjectDetailsQuery,
} from "../../apis/projects/project";
import { setBaseUrl } from "../../utilities/url-utilities";

const store = configureStore({
  reducer: {
    [projectApi.reducerPath]: projectApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(projectApi.middleware),
});

function ActivityLayout() {
  return (
    <Router>
      <ProjectActivity path={setBaseUrl("/projects/:projectId/activity")} />
    </Router>
  );
}

function ProjectActivity({ projectId }) {
  const { data: project = {} } = useGetProjectDetailsQuery(projectId);

  React.useEffect(() => {
    fetch(`/events/project/${projectId}`)
      .then((response) => response.json())
      .then(console.log);
  }, []);

  return (
    <>
      <Typography.Title level={2}>Project Activity</Typography.Title>
    </>
  );
}

render(
  <Provider store={store}>
    <ActivityLayout />
  </Provider>,
  document.querySelector("#root")
);
