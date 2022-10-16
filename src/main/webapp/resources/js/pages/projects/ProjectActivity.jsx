import { Button, Col, List, notification, Row, Space, Typography } from "antd";
import React from "react";
import { createRoot } from "react-dom/client";
import { getProjectActivities } from "../../apis/activities/activities";
import { ActivityListItem } from "../../components/activities/ActivityListItem";
import { BORDERED_LIGHT } from "../../styles/borders";
import { addKeysToList } from "../../utilities/http-utilities";
import { getProjectIdFromUrl } from "../../utilities/url-utilities";

/**
 * Component to display project activities
 * @param {number} projectId - identifier current project
 * @returns {JSX.Element}
 * @constructor
 */
function ProjectActivity() {
  // Get the project id from URL
  const projectId = getProjectIdFromUrl();

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
    getProjectActivities(projectId, page)
      .then((data) => {
        const list = addKeysToList(data.content, "activity", "date");
        setActivities([...activities, ...list]);
        setTotal(data.total);
      })
      .catch((error) => notification.error({ message: error }));
  }, [projectId, page]);

  return (
    <>
      <Typography.Title level={2}>
        {i18n("ProjectActivity.title")}
      </Typography.Title>
      <Row>
        <Col md={24} lg={12}>
          <Space direction={"vertical"} style={{ width: `100%` }}>
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

const root = createRoot(document.querySelector("#root"));
root.render(<ProjectActivity />);
