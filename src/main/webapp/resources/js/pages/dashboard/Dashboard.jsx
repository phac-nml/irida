import { Button, Space, Typography } from "antd";
import React from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../components/page/PageWrapper";

import { RecentActivityAllProjects } from "./components/RecentActivityAllProjects";
import { RecentActivityUserProjects } from "./components/RecentActivityUserProjects";

const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";

/**
 * Component to display dashboard
 * @returns {JSX.Element}
 * @constructor
 */
function Dashboard() {
  const [allProjectsVisible, setAllProjectsVisible] = React.useState(false);
  const [userProjectsVisible, setUserProjectsVisible] = React.useState(true);

  return (
    <PageWrapper
      title={
        <Typography.Title level={2}>
          {allProjectsVisible
            ? i18n("Dashboard.recent.activity.title.allProjectsRecentActivity")
            : i18n(
                "Dashboard.recent.activity.title.yourProjectsRecentActivity"
              )}
        </Typography.Title>
      }
    >
      <Space direction={"vertical"} style={{ width: `100%` }}>
        {isAdmin &&
          (!allProjectsVisible ? (
            <Button
              onClick={() => {
                setAllProjectsVisible(true);
                setUserProjectsVisible(false);
              }}
            >
              {i18n("Dashboard.recent.activity.button.allProjects")}
            </Button>
          ) : !userProjectsVisible ? (
            <Button
              onClick={() => {
                setUserProjectsVisible(true);
                setAllProjectsVisible(false);
              }}
            >
              {i18n("Dashboard.recent.activity.button.yourProjects")}
            </Button>
          ) : null)}

        {allProjectsVisible && isAdmin ? (
          <RecentActivityAllProjects />
        ) : (
          <RecentActivityUserProjects />
        )}
      </Space>
    </PageWrapper>
  );
}

render(<Dashboard />, document.querySelector("#root"));
