/*
 * This file renders the Basic Statistics component
 * with functionality to view advanced statistics
 * charts
 */

import React, { useState } from "react";
import { Button, Card, Statistic, Row, Col } from "antd";
import { SPACE_LG } from "../../../../styles/spacing";
import AdvancedStatistics from "./AdvancedStatistics";
import {
  defaultTimePeriod,
  statisticTypes,
  timePeriodMap
} from "../../../../contexts/AdminStatisticsContext";

export default function BasicStats({ statistics }) {
  const [statsChartView, setStatsChartView] = useState(null);

  return (
    <div className="t-statistics">
      <div style={{marginBottom: SPACE_LG}} className="t-stats-basic">
        <Row gutter={16}>
          <Col span={6}>
            <Card>
              <Statistic
                title={`Analyses run in past ${timePeriodMap[defaultTimePeriod]}`}
                value={statistics.analysesRun}
              />
              <Button
                style={{ marginTop: 16 }}
                onClick={() => setStatsChartView(<AdvancedStatistics statType={statisticTypes.ANALYSES} />)}
              >
                Advanced Analyses Statistics
              </Button>
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic
                title={`Projects created in past ${timePeriodMap[defaultTimePeriod]}`}
                value={statistics.projectsCreated}
              />
              <Button
                style={{ marginTop: 16 }}
                onClick={() => setStatsChartView(<AdvancedStatistics statType={statisticTypes.PROJECTS} />)}
              >
                Advanced Project Statistics
              </Button>
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic
                title={`Samples created in past ${timePeriodMap[defaultTimePeriod]}`}
                value={statistics.samplesCreated}
              />
              <Button
                style={{ marginTop: 16 }}
                onClick={() => setStatsChartView(<AdvancedStatistics statType={statisticTypes.SAMPLES} />)}
              >
                Advanced Sample Statistics
              </Button>
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic
                title={`Users logged in past ${timePeriodMap[defaultTimePeriod]}`}
                value={statistics.usersLoggedIn}
              />
              <Button
                style={{ marginTop: 16 }}
                onClick={() => setStatsChartView(<AdvancedStatistics statType={statisticTypes.USERS} />)}
              >
                Advanced User Statistics
              </Button>
            </Card>
          </Col>
        </Row>
      </div>

      <div className="t-stats-chart">
        {statsChartView}
      </div>
    </div>
  );
}