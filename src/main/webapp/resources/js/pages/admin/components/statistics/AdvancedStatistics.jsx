/*
 * This file renders the Advanced Statistics component
 * which displays statistics in charts
 */

import React, { useContext, useEffect, useState } from "react";
import { Bar, Column, Line, Pie } from "@ant-design/charts";
import { Card, Form, PageHeader } from "antd";
import { TimePeriodSelect } from "./TimePeriodSelect";
import {
  AdminStatisticsContext
} from "../../../../contexts/statistics-context";

import {
  chartTypes,
  defaultChartType,
  defaultTimePeriod,
  statisticTypes
} from "../../statistics-constants";

import { SPACE_LG, SPACE_MD } from "../../../../styles/spacing";
import { ChartTypeButtons } from "./ChartTypeButtons";
import { getChartConfiguration } from "../../chart-config";
import { useNavigate } from "@reach/router";
import { setBaseUrl } from "../../../../utilities/url-utilities";

export default function AdvancedStatistics({ statType }) {
  const {
    adminStatisticsContext,
    getUpdatedStatsForStatType
  } = useContext(AdminStatisticsContext);

  const [chartType, setChartType] = useState(defaultChartType);
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const components = {
    [chartTypes.BAR]: Bar,
    [chartTypes.COLUMN]: Column,
    [chartTypes.LINE]: Line,
    [chartTypes.PIE]: Pie,
  };

  const statTypes = {
    [statisticTypes.ANALYSES]: {
      title: "Number of Analyses Ran",
      data: adminStatisticsContext.statistics.analysesStats
    },
    [statisticTypes.PROJECTS]: {
      title: "Number of Projects Created",
      data: adminStatisticsContext.statistics.projectStats
    },
    [statisticTypes.SAMPLES]: {
      title: "Number of Samples Created",
      data: adminStatisticsContext.statistics.sampleStats
    },
    [statisticTypes.USERS]: {
      title: "Number of Users Created",
      data: adminStatisticsContext.statistics.userStats
    },
  }

  useEffect(() => {
    setChartType(defaultChartType);
    form.setFieldsValue({
      "time-period": defaultTimePeriod,
    });
  }, [statType]);

  function updateTimePeriod(currTimePeriod) {
    getUpdatedStatsForStatType(statType, currTimePeriod);
  }

  function displayChart() {
    const Component = components[chartType];

    return Component ? (
      <Component
        {...getChartConfiguration(
          chartType,
          statTypes[statType].data
        )}
      />
    ) : null;
  }

  return (
    <>
      <PageHeader
        title={statTypes[statType].title}
        onBack={() => navigate(setBaseUrl(`/admin/statistics`))}
      />
      <Card style={{ margin: SPACE_LG }}>
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            "time-period": defaultTimePeriod,
            "chart-type": defaultChartType
          }}
          style={{ marginBottom: SPACE_MD }}
        >
          <TimePeriodSelect onChange={updateTimePeriod} />
          <ChartTypeButtons
            onChange={(e) => setChartType(e.target.value)}
            value={chartType}
          />
        </Form>
        {displayChart()}
      </Card>
    </>
  );
}
