import React from "react";
import { Card, Col, notification, Row, Statistic } from "antd";
import {
  IconDatabaseOutlined,
  IconExperiment,
  IconShare,
} from "../../../components/icons/Icons";
import { fetchUserStatistics } from "../../../apis/dashboard/dashboard";

const userId = window.TL._USER.identifier;

/**
 * Component to display user statistics
 * @returns {JSX.Element}
 * @constructor
 */
export function UserProjectStatistics() {
  const [statistics, setStatistics] = React.useState({});
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    fetchUserStatistics(userId)
      .then((data) => {
        setStatistics(data);
        setLoading(false);
      })
      .catch((error) => {
        notification.error({ message: error.response.data.error });
      });
  }, []);

  return (
    <div>
      <Row gutter={16}>
        <Col span={4}>
          <Card>
            <Statistic
              title="Number of Projects"
              value={statistics.numberOfProjects}
              prefix={<IconDatabaseOutlined />}
              loading={loading}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="Number of Samples"
              value={statistics.numberOfSamples}
              prefix={<IconExperiment />}
              loading={loading}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="Number of Analyses"
              value={statistics.numberOfAnalyses}
              prefix={<IconShare />}
              loading={loading}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
}
