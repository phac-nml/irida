import { Button, Col, List, notification, Row, Space, Typography } from "antd";
import React from "react";
import { render } from "react-dom";
import { ActivityListItem } from "../../components/activities/ActivityListItem";
import { BORDERED_LIGHT } from "../../styles/borders";
import { addKeysToList } from "../../utilities/http-utilities";
import { getUserActivities } from "../../apis/activities/activities";

/**
 * Component to display user activities
 * @returns {JSX.Element}
 * @constructor
 */
function RecentActivities() {
  /**
   * List of activities to render
   */
  const [activities, setActivities] = React.useState([]);

  /**
   * Total number of activities associated with this user
   */
  const [total, setTotal] = React.useState(0);

  /**
   * Last loaded page of activities from the server
   */
  const [page, setPage] = React.useState(0);

  React.useEffect(() => {
    getUserActivities({ page })
      .then((data) => {
        const list = addKeysToList(data.content, "activity", "date");
        setActivities([...activities, ...list]);
        setTotal(data.total);
      })
      .catch((error) => notification.error({ message: error }));
  }, [page]);

  return (
    <>
      <Typography.Title level={2}>Recent Activities</Typography.Title>
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
        }}
      >
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
              Load More
            </Button>
            <Typography.Text>
              {i18n("ProjectActivity.loaded", activities.length, total)}
            </Typography.Text>
          </Space>
        </Space>
      </div>
    </>
  );
}

render(<RecentActivities />, document.querySelector("#root"));
