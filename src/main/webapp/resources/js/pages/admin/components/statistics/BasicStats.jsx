/*
 * This file renders the Basic Statistics component
 * with functionality to view advanced statistics
 * charts
 */

import React, { useContext, useState } from "react";
import { Button, Card, Statistic } from "antd";
import { SPACE_LG, SPACE_XS } from "../../../../styles/spacing";
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

        <div style={{display: "flex",
          justifyContent: "flex-start", flexWrap: "wrap"}}>
            <Card style={{width: 240, marginRight: SPACE_XS}}>
              <Statistic
                title={`Analyses run in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.analysesRan}
              />
              <Button
                style={{ marginTop: 16, width: 170 }}
                onClick={() =>
                {
                  updateAnalysesStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.ANALYSES} />)
                }}
              >
                Analyses Statistics
              </Button>
            </Card>

            <Card style={{width: 240, marginRight: SPACE_XS}}>
              <Statistic
                title={`Projects created in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.projectsCreated}
              />
              <Button
                style={{ marginTop: 16, width: 170 }}
                onClick={() => {
                  updateProjectStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.PROJECTS}/>)
                }}
              >
                Project Statistics
              </Button>
            </Card>

            <Card style={{width: 240, marginRight: SPACE_XS}}>
              <Statistic
                title={`Samples created in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.samplesCreated}
              />
              <Button
                style={{ marginTop: 16, width: 170 }}
                onClick={() => {
                  updateSampleStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.SAMPLES} />)
                }}
              >
                Sample Statistics
              </Button>
            </Card>

            <Card style={{width: 240, marginRight: SPACE_XS}}>
              <Statistic
                title={`Users created in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.usersCreated}
              />
              <Button
                style={{ marginTop: 16, width: 170 }}
                onClick={() => {
                  updateUserStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.USERS} />)
                }}
              >
                User Statistics
              </Button>
            </Card>

            <Card style={{width: 240, marginRight: SPACE_XS}}>
              <Statistic
                title={`Users logged on in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.usersLoggedIn}
              />
              <Button
                style={{ marginTop: 16, width: 170 }}
                disabled={true}
              >
                User Usage Statistics
              </Button>
            </Card>
        </div>
      </div>

      <div className="t-stats-chart">
        {statsChartView}
      </div>
    </div>
  );
}