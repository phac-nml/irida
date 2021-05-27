import { Router } from "@reach/router";
import { Button, Col, List, Row, Space, Typography } from "antd";
import React from "react";
import { render } from "react-dom";
import { getProjectActivities } from "../../apis/activities/activities";
import { ActivityListItem } from "../../components/activities/ActivityListItem";
import { BORDERED_LIGHT } from "../../styles/borders";
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
  /**
   * List of activities to render
   */
  const [activities, setActivities] = React.useState([]);

  /**
   * Total number of ativities associated with this project
   */
  const [total, setTotal] = React.useState(0);

  /**
   * Last loaded page of activities from the server
   */
  const [page, setPage] = React.useState(0);

  React.useEffect(() => {
    getProjectActivities({ projectId, page }).then((data) => {
      const list = addKeysToList(data.content, "activity", "date");
      setActivities([...activities, ...list]);
      setTotal(data.total);
    });
  }, [projectId, page]);

  return (
    <>
      <Typography.Title level={2}>
        {i18n("ProjectActivity.title")}
      </Typography.Title>
      <Row>
        <Col md={24} lg={12}>
          <Space direction={"vertical"} style={{ display: "block" }}>
            <div
              style={{
                maxHeight: 600,
                overflow: "auto",
                border: BORDERED_LIGHT,
                borderLeft: "none",
                borderRight: "none",
              }}
            >
              <List
                bordered
                dataSource={activities}
                renderItem={(activity) => (
                  <ActivityListItem activity={activity} />
                )}
              />
            </div>
            <Space>
              <Button
                className={"t-load-more"}
                onClick={() => setPage(page + 1)}
                disabled={total === activities.length}
              >
                {i18n("ProjectActivity.load-more")}
              </Button>
              <Typography.Text>
                {i18n("ProjectActivity.loaded", activities.length, total)}
              </Typography.Text>
            </Space>
          </Space>
        </Col>
      </Row>
    </>
  );
}

render(<ActivityLayout />, document.querySelector("#root"));
