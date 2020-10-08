/*
 * This file renders the Basic Statistics component
 * with functionality to view advanced statistics
 * charts
 */

import React, { useContext, useState } from "react";
import { Button, Card, Divider, Statistic } from "antd";
import { SPACE_LG, SPACE_XS } from "../../../../styles/spacing";
import AdvancedStatistics from "./AdvancedStatistics";
import {
  AdminStatisticsContext,
  defaultTimePeriod,
  statisticTypes,
  timePeriodMap
} from "../../../../contexts/AdminStatisticsContext";
import styled from "styled-components";

export default function BasicStats() {
  const [statsChartView, setStatsChartView] = useState(null);

  const {
    adminStatisticsContext,
    updateAnalysesStatsTimePeriod,
    updateProjectStatsTimePeriod,
    updateSampleStatsTimePeriod,
    updateUserStatsTimePeriod,
  } = useContext(AdminStatisticsContext);

  const StatisticsCard = styled(Card)`
    border: none;
    width: 240px;
    margin-right: ${SPACE_XS}
  `;

  const StatisticsButton = styled(Button)`
    width: 170px;
    margin-top: ${SPACE_XS}
  `;

  return (
    <div className="t-statistics">
      <div style={{marginBottom: SPACE_LG}} className="t-stats-basic">

        <div style={{display: "flex",
          justifyContent: "space-between", flexWrap: "wrap"}}>
            <StatisticsCard>
              <Statistic
                title={`Analyses run in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.analysesRan}
              />
              <StatisticsButton
                onClick={() =>
                {
                  updateAnalysesStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.ANALYSES} />)
                }}
              >
                Analyses Statistics
              </StatisticsButton>
            </StatisticsCard>

            <StatisticsCard>
              <Statistic
                title={`Projects created in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.projectsCreated}
              />
              <StatisticsButton
                onClick={() => {
                  updateProjectStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.PROJECTS}/>)
                }}
              >
                Project Statistics
              </StatisticsButton>
            </StatisticsCard>

            <StatisticsCard>
              <Statistic
                title={`Samples created in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.samplesCreated}
              />
              <StatisticsButton
                onClick={() => {
                  updateSampleStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.SAMPLES} />)
                }}
              >
                Sample Statistics
              </StatisticsButton>
            </StatisticsCard>

            <StatisticsCard>
              <Statistic
                title={`Users created in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.usersCreated}
              />
              <StatisticsButton
                onClick={() => {
                  updateUserStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.USERS} />)
                }}
              >
                User Statistics
              </StatisticsButton>
            </StatisticsCard>

            <StatisticsCard>
              <Statistic
                title={`Users logged on in past ${timePeriodMap[defaultTimePeriod]}`}
                value={adminStatisticsContext.basicStats.usersLoggedIn}
              />
              <StatisticsButton
                disabled={true}
              >
                User Usage Statistics
              </StatisticsButton>
            </StatisticsCard>
        </div>
      </div>

      <Divider />

      <div className="t-stats-chart">
        {statsChartView}
      </div>
    </div>
  );
}