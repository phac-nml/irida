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

  useEffect(() => {
    setChartType(defaultChartType);
    form.setFieldsValue({
      "time-period": defaultTimePeriod,
    });
  }, [statType]);

  function updateTimePeriod(currTimePeriod) {
    getUpdatedStatsForStatType(statType, currTimePeriod);
  }

  const chartTitle =
    statType === statisticTypes.ANALYSES
      ? "Number of Analyses Ran"
      : statType === statisticTypes.PROJECTS
      ? "Number of Projects Created"
      : statType === statisticTypes.SAMPLES
      ? "Number of Samples Created"
      : statType === statisticTypes.USERS
      ? "Number of Users Created"
      : null;

  function displayChart() {
    const Component = components[chartType];
    let data = [{"key":"", "value":""}];

    if(statType === statisticTypes.ANALYSES) {
      data = adminStatisticsContext.statistics.analysesStats;
    } else if (statType === statisticTypes.PROJECTS) {
      data = adminStatisticsContext.statistics.projectStats;
    } else if (statType === statisticTypes.SAMPLES) {
      data = adminStatisticsContext.statistics.sampleStats;
    } else if (statType === statisticTypes.USERS) {
      data = adminStatisticsContext.statistics.userStats;
    }

    return Component ? (
      <Component
        {...getChartConfiguration(
          chartType,
          data
        )}
      />
    ) : null;
  }

  return (
    <>
      <PageHeader
        title={chartTitle}
        onBack={() => navigate(setBaseUrl(`/admin/statistics`))}
      />
      <Card style={{ margin: SPACE_LG }}>
        <Form
          form={form}
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
