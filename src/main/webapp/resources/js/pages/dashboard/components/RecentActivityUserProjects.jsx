import { notification } from "antd";
import React from "react";
import { addKeysToList } from "../../../utilities/http-utilities";
import { getUserActivities } from "../../../apis/activities/activities";
import { RecentActivityList } from "./RecentActivityList";

/**
 * Component to display user projects recent activity
 * @returns {JSX.Element}
 * @constructor
 */
export function RecentActivityUserProjects() {
  /**
   * List of recent activities to render
   */
  const [activities, setActivities] = React.useState([]);

  /**
   * Total number of recent activities for all projects associated with this user
   */
  const [total, setTotal] = React.useState(0);

  /**
   * Last loaded page of activities from the server
   */
  const [page, setPage] = React.useState(0);

  /**
   * Whether the data is still being retrieved from the server or not
   */
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    getUserActivities({ page })
      .then((data) => {
        const list = addKeysToList(data.content, "activity", "date");
        setActivities([...activities, ...list]);
        setTotal(data.total);
        setLoading(false);
      })
      .catch((error) => notification.error({ message: error }));
  }, [page]);

  return (
    <RecentActivityList
      activities={activities}
      total={total}
      page={page}
      setPage={setPage}
      loading={loading}
    />
  );
}
