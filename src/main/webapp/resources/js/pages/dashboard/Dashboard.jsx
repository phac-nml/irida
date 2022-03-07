import React from "react";
import { Space } from "antd";
import { render } from "react-dom";
import { PageWrapper } from "../../components/page/PageWrapper";
import { UserProjectStatistics } from "./components/UserProjectStatistics";
import { RecentActivity } from "./components/RecentActivity";

/**
 * Component to display dashboard
 * @returns {JSX.Element}
 * @constructor
 */
function Dashboard() {
  return (
    <PageWrapper>
      <Space direction="vertical" size="large" style={{ width: "100%" }}>
        <UserProjectStatistics />
        <RecentActivity />
      </Space>
    </PageWrapper>
  );
}

render(<Dashboard />, document.querySelector("#root"));
