/*
 * This file renders the Basic Statistics component
 * with functionality to view advanced statistics
 * charts
 */

import React, { useContext, useState } from "react";
import { Button, Card, Statistic, Row, Col } from "antd";
import { SPACE_LG } from "../../../../styles/spacing";
import AdvancedStatistics from "./AdvancedStatistics";
import {
  AdminStatisticsContext,
  defaultTimePeriod,
  statisticTypes,
  timePeriodMap
} from "../../../../contexts/AdminStatisticsContext";

export default function BasicStats() {
  const [statsChartView, setStatsChartView] = useState(null);

  const {
    adminStatisticsContext,
    updateAnalysesStatsTimePeriod,
    updateProjectStatsTimePeriod,
    updateSampleStatsTimePeriod,
    updateUserStatsTimePeriod,
  } = useContext(AdminStatisticsContext);

  return (
    <div className="t-statistics">
      <div style={{marginBottom: SPACE_LG}} className="t-stats-basic">
        <Row gutter={16}>
          <Col span={4}>
            <Card>
              <Statistic
                title={`Analyses run in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.analysesRan}
              />
              <Button
                style={{ marginTop: 16 }}
                onClick={() =>
                {
                  updateAnalysesStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.ANALYSES} />)
                }}
              >
                Advanced Analyses Statistics
              </Button>
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic
                title={`Projects created in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.projectsCreated}
              />
              <Button
                style={{ marginTop: 16 }}
                onClick={() => {
                  updateProjectStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.PROJECTS}/>)
                }
                }
              >
                Advanced Project Statistics
              </Button>
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic
                title={`Samples created in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.samplesCreated}
              />
              <Button
                style={{ marginTop: 16 }}
                onClick={() => {
                  updateSampleStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.SAMPLES} />)
                }}
              >
                Advanced Sample Statistics
              </Button>
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic
                title={`Users created in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.usersCreated}
              />
              <Button
                style={{ marginTop: 16 }}
                onClick={() => {
                  updateUserStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.USERS} />)
                }}
              >
                Advanced User Statistics
              </Button>
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic
                title={`Users logged in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.usersLoggedIn}
              />
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