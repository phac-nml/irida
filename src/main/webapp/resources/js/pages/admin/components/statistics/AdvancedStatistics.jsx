/*
 * This file renders the Advanced Statistics component
 * which displays statistics in charts
 */

import React, { useContext, useEffect, useState } from "react";
import { Bar, Column, Line, Pie } from "@ant-design/charts";
import { Card, Form, PageHeader } from "antd";
import { TimePeriodSelect } from "./TimePeriodSelect";
import {
  AdminStatisticsContext,
  chartTypes,
  defaultChartType,
  defaultTimePeriod,
  statisticTypes,
} from "../../../../contexts/AdminStatisticsContext";

import { SPACE_LG, SPACE_MD } from "../../../../styles/spacing";
import { ChartTypeButtons } from "./ChartTypeButtons";
import { getChartConfiguration } from "../../chart-config";
import { useNavigate } from "@reach/router";
import { setBaseUrl } from "../../../../utilities/url-utilities";

export default function AdvancedStatistics({ statType }) {
  const {
    adminStatisticsContext,
    updateAnalysesStatsTimePeriod,
    updateProjectStatsTimePeriod,
    updateSampleStatsTimePeriod,
    updateUserStatsTimePeriod,
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

  /*
   * Updates the stats for the time period selected
   * for the stat type (analyses, projects, samples, or users
   */
  function updateTimePeriod(currTimePeriod) {
    if (statType === statisticTypes.ANALYSES) {
      updateAnalysesStatsTimePeriod(currTimePeriod);
    } else if (statType === statisticTypes.PROJECTS) {
      updateProjectStatsTimePeriod(currTimePeriod);
    } else if (statType === statisticTypes.SAMPLES) {
      updateSampleStatsTimePeriod(currTimePeriod);
    } else if (statType === statisticTypes.USERS) {
      updateUserStatsTimePeriod(currTimePeriod);
    }
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
      : null
  ;

  // Displays the data in a chart of type selected
  function displayChart() {
    const Component = components[chartType];
    return Component ? (
      <Component
        {...getChartConfiguration(
          chartType,
          statType,
          adminStatisticsContext.statistics
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
          }}
          style={{ marginBottom: SPACE_MD }}
        >
          <TimePeriodSelect onChange={(e) => updateTimePeriod(e)} />
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
