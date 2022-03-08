import { Button, Space, Typography } from "antd";
import React from "react";
import { grey1 } from "../../../styles/colors";
import { SPACE_MD } from "../../../styles/spacing";
import { RecentActivityAllProjects } from "./RecentActivityAllProjects";
import { RecentActivityUserProjects } from "./RecentActivityUserProjects";

const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";

/**
 * Component to display recent activity
 * @returns {JSX.Element}
 * @constructor
 */
export function RecentActivity() {
  const [allProjectsVisible, setAllProjectsVisible] = React.useState(false);
  const [userProjectsVisible, setUserProjectsVisible] = React.useState(true);

  return (
    <Space
      direction={"vertical"}
      style={{ width: `100%`, backgroundColor: grey1, padding: SPACE_MD }}
    >
      <Typography.Title level={4} className="t-recent-activity-title">
        {allProjectsVisible
          ? i18n("RecentActivity.title.allProjectsRecentActivity")
          : i18n("RecentActivity.title.yourProjectsRecentActivity")}
      </Typography.Title>
      {isAdmin &&
        (!allProjectsVisible ? (
          <Button
            onClick={() => {
              setAllProjectsVisible(true);
              setUserProjectsVisible(false);
            }}
            className="t-all-projects-button"
          >
            {i18n("RecentActivity.button.allProjects")}
          </Button>
        ) : !userProjectsVisible ? (
          <Button
            onClick={() => {
              setUserProjectsVisible(true);
              setAllProjectsVisible(false);
            }}
            className="t-your-projects-button"
          >
            {i18n("RecentActivity.button.yourProjects")}
          </Button>
        ) : null)}

      {allProjectsVisible && isAdmin ? (
        <RecentActivityAllProjects />
      ) : (
        <RecentActivityUserProjects />
      )}
    </Space>
  );
}
