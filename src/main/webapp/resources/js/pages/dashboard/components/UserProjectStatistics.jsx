import React from "react";
import { Card, Col, notification, Row, Statistic } from "antd";
import {
  IconDatabaseOutlined,
  IconExperiment,
  IconShare,
} from "../../../components/icons/Icons";
import { fetchUserStatistics } from "../../../apis/dashboard/dashboard";
import { blue6 } from "../../../styles/colors";

const userId = window.TL._USER.identifier;

const StatCol = ({ children }) => <Col span={8}>{children}</Col>;

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
    <Row gutter={16} justify="space-between">
      <StatCol>
        <Card>
          <Statistic
            title="Number of Projects"
            valueStyle={{ color: blue6 }}
            value={statistics.numberOfProjects}
            prefix={<IconDatabaseOutlined />}
            loading={loading}
          />
        </Card>
      </StatCol>
      <StatCol>
        <Card>
          <Statistic
            title="Number of Samples"
            valueStyle={{ color: blue6 }}
            value={statistics.numberOfSamples}
            prefix={<IconExperiment />}
            loading={loading}
          />
        </Card>
      </StatCol>
      <StatCol>
        <Card>
          <Statistic
            title="Number of Analyses"
            valueStyle={{ color: blue6 }}
            value={statistics.numberOfAnalyses}
            prefix={<IconShare />}
            loading={loading}
          />
        </Card>
      </StatCol>
    </Row>
  );
}
