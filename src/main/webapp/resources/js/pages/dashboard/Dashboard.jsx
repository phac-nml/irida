import React from "react";
import { Col, Layout, Row } from "antd";
import { render } from "react-dom";
import { UserProjectStatistics } from "./components/UserProjectStatistics";
import { RecentActivity } from "./components/RecentActivity";
import { SPACE_MD } from "../../styles/spacing";

const { Content } = Layout;

/**
 * Component to display dashboard
 * @returns {JSX.Element}
 * @constructor
 */
function Dashboard() {
  return (
    <Layout style={{ height: "100%", minHeight: "100%" }}>
      <Row>
        <Col sm={24} md={{ span: 20, offset: 2 }} xl={{ span: 12, offset: 6 }}>
          <Content style={{ padding: SPACE_MD }}>
            <Row gutter={[16, 16]}>
              <Col span={24}>
                <UserProjectStatistics />
              </Col>
              <Col span={24}>
                <RecentActivity />
              </Col>
            </Row>
          </Content>
        </Col>
      </Row>
    </Layout>
  );
}

render(<Dashboard />, document.querySelector("#root"));
