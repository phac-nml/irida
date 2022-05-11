import { Button, Col, Row, Typography } from "antd";
import React from "react";

import { RecentActivityAllProjects } from "./RecentActivityAllProjects";
import { RecentActivityUserProjects } from "./RecentActivityUserProjects";
import { isAdmin } from "../../../utilities/role-utilities";
import { grey1 } from "../../../styles/colors";
import { SPACE_XS } from "../../../styles/spacing";

/**
 * Component to display recent activity
 * @returns {JSX.Element}
 * @constructor
 */
export function RecentActivity() {
  const [allProjectsVisible, setAllProjectsVisible] = React.useState(false);
  const [userProjectsVisible, setUserProjectsVisible] = React.useState(true);
  const admin = isAdmin();

  return (
    <Row style={{ backgroundColor: grey1, padding: 10 }}>
      <Col span={24}>
        <Typography.Title level={4} className="t-recent-activity-title">
          {allProjectsVisible
            ? i18n("RecentActivity.title.allProjectsRecentActivity")
            : i18n("RecentActivity.title.yourProjectsRecentActivity")}
        </Typography.Title>
      </Col>
      <Col span={24} style={{ marginBottom: SPACE_XS }}>
        {admin &&
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
      </Col>
      <Col span={24}>
        {allProjectsVisible && admin ? (
          <RecentActivityAllProjects />
        ) : (
          <RecentActivityUserProjects />
        )}
      </Col>
    </Row>
  );
}
