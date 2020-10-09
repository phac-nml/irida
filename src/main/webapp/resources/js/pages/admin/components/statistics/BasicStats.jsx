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

const StatisticsCard = styled(Card)`
  border: none;
  width: 240px;
  margin-right: ${SPACE_XS};
  margin-bottom: ${SPACE_XS};
`;

const StatisticsButton = styled(Button)`
  width: 170px;
  margin-top: ${SPACE_XS};
`;

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
          justifyContent: "space-between", flexWrap: "wrap"}}>
            <StatisticsCard>
              <Statistic
                title={i18n("AdminPanelStatistics.titleAnalyses", timePeriodMap[defaultTimePeriod])}
                value={adminStatisticsContext.basicStats.analysesRan}
              />
              <StatisticsButton
                onClick={() =>
                {
                  updateAnalysesStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.ANALYSES} />)
                }}
              >
                {i18n("AdminPanelStatistics.buttonAnalyses")}
              </StatisticsButton>
            </StatisticsCard>

            <StatisticsCard>
              <Statistic
                title={i18n("AdminPanelStatistics.titleProjects", timePeriodMap[defaultTimePeriod])}
                value={adminStatisticsContext.basicStats.projectsCreated}
              />
              <StatisticsButton
                onClick={() => {
                  updateProjectStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.PROJECTS}/>)
                }}
              >
                {i18n("AdminPanelStatistics.buttonProjects")}
              </StatisticsButton>
            </StatisticsCard>

            <StatisticsCard>
              <Statistic
                title={i18n("AdminPanelStatistics.titleSamples", timePeriodMap[defaultTimePeriod])}
                value={adminStatisticsContext.basicStats.samplesCreated}
              />
              <StatisticsButton
                onClick={() => {
                  updateSampleStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.SAMPLES} />)
                }}
              >
                {i18n("AdminPanelStatistics.buttonSamples")}
              </StatisticsButton>
            </StatisticsCard>

            <StatisticsCard>
              <Statistic
                title={i18n("AdminPanelStatistics.titleUsersCreated", timePeriodMap[defaultTimePeriod])}
                value={adminStatisticsContext.basicStats.usersCreated}
              />
              <StatisticsButton
                onClick={() => {
                  updateUserStatsTimePeriod(defaultTimePeriod)
                  setStatsChartView(<AdvancedStatistics statType={statisticTypes.USERS} />)
                }}
              >
                {i18n("AdminPanelStatistics.buttonUsers")}
              </StatisticsButton>
            </StatisticsCard>

            <StatisticsCard>
              <Statistic
                title={i18n("AdminPanelStatistics.titleUsersLoggedOn", timePeriodMap[defaultTimePeriod])}
                value={adminStatisticsContext.basicStats.usersLoggedIn}
              />
              <StatisticsButton
                disabled={true}
              >
                {i18n("AdminPanelStatistics.buttonUserUsage")}
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