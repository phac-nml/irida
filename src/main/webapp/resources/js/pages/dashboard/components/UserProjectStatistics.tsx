import React from "react";
import { Card, Col, notification, Row, Statistic } from "antd";
import {
  IconDatabaseOutlined,
  IconExperiment,
  IconShare,
} from "../../../components/icons/Icons";
import {
  fetchUserStatistics,
  UserStatistics,
} from "../../../apis/dashboard/dashboard";
import { blue6 } from "../../../styles/colors";

declare let window: IridaWindow;

const userId = window.TL._USER.identifier;

const StatCol = ({ children }: { children: React.ReactNode }): JSX.Element => (
  <Col span={8}>{children}</Col>
);

/**
 * Component to display user statistics
 * @returns {JSX.Element}
 * @constructor
 */
export function UserProjectStatistics(): JSX.Element {
  const [statistics, setStatistics] = React.useState<UserStatistics>();
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
            title={i18n("UserProjectStatistics.numProjectsTitle")}
            valueStyle={{ color: blue6 }}
            value={statistics?.numberOfProjects}
            prefix={<IconDatabaseOutlined />}
            loading={loading}
          />
        </Card>
      </StatCol>
      <StatCol>
        <Card>
          <Statistic
            title={i18n("UserProjectStatistics.numSamplesTitle")}
            valueStyle={{ color: blue6 }}
            value={statistics?.numberOfSamples}
            prefix={<IconExperiment />}
            loading={loading}
          />
        </Card>
      </StatCol>
      <StatCol>
        <Card>
          <Statistic
            title={i18n("UserProjectStatistics.numAnalysesTitle")}
            valueStyle={{ color: blue6 }}
            value={statistics?.numberOfAnalyses}
            prefix={<IconShare />}
            loading={loading}
          />
        </Card>
      </StatCol>
    </Row>
  );
}
