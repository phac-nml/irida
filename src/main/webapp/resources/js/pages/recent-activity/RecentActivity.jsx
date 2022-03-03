import { Button, Space, Typography } from "antd";
import React from "react";
import { render } from "react-dom";
import { PageWrapper } from "../../components/page/PageWrapper";

import { RecentActivityAllProjects } from "./components/RecentActivityAllProjects";
import { RecentActivityUserProjects } from "./components/RecentActivityUserProjects";

const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";

/**
 * Component to display user activity
 * @returns {JSX.Element}
 * @constructor
 */
function RecentActivity() {
  const [allProjectsVisible, setAllProjectsVisible] = React.useState(false);
  const [userProjectsVisibile, setUserProjectsVisible] = React.useState(true);

  return (
    <PageWrapper
      title={
        allProjectsVisible ? (
          <Typography.Title level={2}>
            {i18n("RecentActivity.title.allProjectsRecentActivity")}
          </Typography.Title>
        ) : (
          <Typography.Title level={2}>
            {i18n("RecentActivity.title.yourProjectsRecentActivity")}
          </Typography.Title>
        )
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
              {i18n("RecentActivity.button.allProjects")}
            </Button>
          ) : !userProjectsVisibile ? (
            <Button
              onClick={() => {
                setUserProjectsVisible(true);
                setAllProjectsVisible(false);
              }}
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
    </PageWrapper>
  );
}

render(<RecentActivity />, document.querySelector("#root"));
