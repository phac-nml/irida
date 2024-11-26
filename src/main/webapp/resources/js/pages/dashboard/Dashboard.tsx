import React from "react";
import { Col, Layout, Row } from "antd";
import { createRoot } from 'react-dom/client';
import { UserProjectStatistics } from "./components/UserProjectStatistics";
import { RecentActivity } from "./components/RecentActivity";
import { SPACE_MD } from "../../styles/spacing";

const { Content } = Layout;

/**
 * Component to display dashboard
 * @returns {JSX.Element}
 * @constructor
 */
const Dashboard = (): JSX.Element => {
  return (
    <Layout style={{ minHeight: "100%" }}>
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
};

const container = document.getElementById('root');
const root = createRoot(container);
root.render(<Dashboard />);
