/*
 * This file renders the Basic Statistics component
 * with functionality to view advanced statistics
 * charts
 */

import React, { useContext, useState } from "react";
import { Col, Card, Row, Statistic } from "antd";
import { SPACE_LG } from "../../../../styles/spacing";
import { TinyColumn } from "@ant-design/charts";
import AdvancedStatistics from "./AdvancedStatistics";
import {
  AdminStatisticsContext, chartTypes,
  defaultTimePeriod,
  statisticTypes,
  timePeriodMap
} from "../../../../contexts/AdminStatisticsContext";
import { getChartConfiguration } from "../../chart-config";

export default function BasicStats() {
  const [statsChartView, setStatsChartView] = useState(null);
  const { adminStatisticsContext } = useContext(
    AdminStatisticsContext
  );

  const cards = [
    {
      key: `analyses`,
      title: `Analyses run in past ${timePeriodMap[defaultTimePeriod]}`,
      value: adminStatisticsContext.basicStats.analysesRun,
      onClick: () =>
        setStatsChartView(
          <AdvancedStatistics statType={statisticTypes.ANALYSES} />
        ),
    },
    {
      key: `projects`,
      title: `Projects created in past ${timePeriodMap[defaultTimePeriod]}`,
      value: adminStatisticsContext.basicStats.projectsCreated,
      onClick: () =>
        setStatsChartView(
          <AdvancedStatistics statType={statisticTypes.PROJECTS} />
        ),
    },
    {
      key: `samples`,
      title: `Samples created in past ${timePeriodMap[defaultTimePeriod]}`,
      value: adminStatisticsContext.basicStats.samplesCreated,
      onClick: () =>
        setStatsChartView(
          <AdvancedStatistics statType={statisticTypes.SAMPLES} />
        ),
    },
    {
      key: `users`,
      title: `Users created in past ${timePeriodMap[defaultTimePeriod]}`,
      value: adminStatisticsContext.basicStats.usersCreated,
      onClick: () =>
        setStatsChartView(
          <AdvancedStatistics statType={statisticTypes.USERS} />
        ),
    },
    {
      key: `usersLoggedIn`,
      title: `Users logged on in past ${timePeriodMap[defaultTimePeriod]}`,
      value: adminStatisticsContext.basicStats.usersLoggedIn,
    },
  ];
  return (
    <div className="t-statistics">
      <div style={{ marginBottom: SPACE_LG }} className="t-stats-basic">
        <Row gutter={[16, 16]}>
          {cards.map((card) => (
            <Col sm={24} md={12} xl={8} xxl={6} key={card.key}>
              <Card onClick={card.onClick}>
                <Statistic title={card.title} value={card.value} />
                <TinyColumn {...getChartConfiguration(chartTypes.TINYCOLUMN, card.key, defaultTimePeriod, adminStatisticsContext.statistics)} />
              </Card>
            </Col>
          ))}
        </Row>
      </div>
      <div className="t-stats-chart">{statsChartView}</div>
    </div>
  );
}