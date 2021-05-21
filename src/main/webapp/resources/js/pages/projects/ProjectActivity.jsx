import { Router } from "@reach/router";
import { List, Typography } from "antd";
import React from "react";
import { render } from "react-dom";
import { getProjectActivities } from "../../apis/activities/activities";
import { ActivityListItem } from "../../components/activities/ActivityListItem";
import { addKeysToList } from "../../utilities/http-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * Layout component solely for the purpose of extracting the project id out of
 * the url.
 * @returns {JSX.Element}
 * @constructor
 */
function ActivityLayout() {
  return (
    <Router>
      <ProjectActivity path={setBaseUrl("/projects/:projectId/activity")} />
    </Router>
  );
}

/**
 * Component to display project activities
 * @param {number} projectId - identifier current project
 * @returns {JSX.Element}
 * @constructor
 */
function ProjectActivity({ projectId }) {
  const [activities, setActivities] = React.useState();

  React.useEffect(() => {
    getProjectActivities({ projectId }).then((data) => {
      const list = addKeysToList(data, "activity");
      setActivities(list);
    });
  }, [projectId]);

  return (
    <>
      <Typography.Title level={2}>
        {i18n("ProjectActivity.title")}
      </Typography.Title>
      <List
        bordered
        dataSource={activities}
        renderItem={(activity) => <ActivityListItem activity={activity} />}
      />
    </>
  );
}

render(<ActivityLayout />, document.querySelector("#root"));
