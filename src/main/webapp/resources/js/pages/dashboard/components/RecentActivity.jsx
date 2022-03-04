import { Button, Space, Typography } from "antd";
import React from "react";
import { RecentActivityAllProjects } from "./RecentActivityAllProjects";
import { RecentActivityUserProjects } from "./RecentActivityUserProjects";

const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";

export function RecentActivity() {
  const [allProjectsVisible, setAllProjectsVisible] = React.useState(false);
  const [userProjectsVisible, setUserProjectsVisible] = React.useState(true);

  return (
    <Space direction={"vertical"} style={{ width: `100%` }}>
      <Typography.Title level={2} className="t-recent-activity-title">
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
