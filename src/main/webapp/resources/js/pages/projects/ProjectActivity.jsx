import { Router } from "@reach/router";
import { Button, List, Space, Typography } from "antd";
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
  const [activities, setActivities] = React.useState([]);
  const [page, setPage] = React.useState(0);

  React.useEffect(() => {
    getProjectActivities({ projectId, page }).then((data) => {
      const list = addKeysToList(data, "activity");
      setActivities([...activities, ...list]);
    });
  }, [projectId, page]);

  return (
    <>
      <Typography.Title level={2}>
        {i18n("ProjectActivity.title")}
      </Typography.Title>
      <Space direction={"vertical"} style={{ display: "block" }}>
        <List
          bordered
          dataSource={activities}
          renderItem={(activity) => <ActivityListItem activity={activity} />}
        />
        <div style={{ display: "flex", justifyContent: "middle" }}>
          <Button size={"small"} onClick={() => setPage(1)}>
            Load More
          </Button>
        </div>
      </Space>
    </>
  );
}

render(<ActivityLayout />, document.querySelector("#root"));
