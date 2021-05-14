import { Router } from "@reach/router";
import { configureStore } from "@reduxjs/toolkit";
import { Avatar, List, Typography } from "antd";
import isNumeric from "antd/es/_util/isNumeric";
import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { getProjectActivities } from "../../apis/activities/activities";
import {
  projectApi,
  useGetProjectDetailsQuery,
} from "../../apis/projects/project";
import { IconCalendarTwoTone, IconUser } from "../../components/icons/Icons";
import { SPACE_XS } from "../../styles/spacing";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
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

  function formatActivity(activity) {
    const texts = activity.sentence.split(/{([0-9])}/);
    const content = [];
    for (let i = 0; i < texts.length; i++) {
      if (isNumeric(texts[i])) {
        const item = activity.items[parseInt(texts[i])];
        if (item.href) {
          content.push(
            <Typography.Link type="link" href={setBaseUrl(item.href)}>
              {item.label}
            </Typography.Link>
          );
        } else {
          content.push(<Typography.Text strong>{item.label}</Typography.Text>);
        }
      } else if (texts[i].length) {
        content.push(texts[i]);
      }
    }
    return content;
  }

  React.useEffect(() => {
    getProjectActivities({ projectId }).then((data) => {
      const list = addKeysToList(
        data.filter((i) => i !== null),
        "activity",
        "date"
      );
      setActivities(list);
    });
  }, []);

  return (
    <>
      <Typography.Title level={2}>Project Activity</Typography.Title>
      <List
        dataSource={activities}
        renderItem={(activity) => (
          <List.Item>
            <List.Item.Meta
              avatar={<Avatar icon={<IconUser />} />}
              title={formatActivity(activity)}
              description={
                <div>
                  <IconCalendarTwoTone style={{ marginRight: SPACE_XS }} />
                  {formatInternationalizedDateTime(activity.date)}
                </div>
              }
            />
          </List.Item>
        )}
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
