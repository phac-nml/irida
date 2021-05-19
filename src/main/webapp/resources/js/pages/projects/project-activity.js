import { Router } from "@reach/router";
import { configureStore } from "@reduxjs/toolkit";
import { List, Typography } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { getProjectActivities } from "../../apis/activities/activities";
import {
  projectApi,
  useGetProjectDetailsQuery,
} from "../../apis/projects/project";
import { ActivityListItem } from "../../components/activities/ActivityListItem";
import { addKeysToList } from "../../utilities/http-utilities";
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
  const [activities, setActivities] = React.useState();

  React.useEffect(() => {
    getProjectActivities({ projectId }).then((data) => {
      const list = addKeysToList(
        data.filter((i) => i !== null),
        "activity",
        "date"
      );
      setActivities(list);
    });
  }, [projectId]);

  return (
    <>
      <Typography.Title level={2}>Project Activity</Typography.Title>
      <List
        dataSource={activities}
        renderItem={(activity) => <ActivityListItem activity={activity} />}
      />
    </>
  );
}

render(
  <Provider store={store}>
    <ActivityLayout />
  </Provider>,
  document.querySelector("#root")
);
