/*
 * This file renders the Advanced Statistics component
 * which displays statistics in charts
 */

import React, { useContext, useEffect, useState } from "react";
import { Bar, Column, Line, Pie } from "@ant-design/charts";
import { Form } from "antd";
import { TimePeriodSelect } from "./TimePeriodSelect";
import {
  AdminStatisticsContext,
  defaultTimePeriod,
  defaultChartType,
  chartTypes,
  statisticTypes
} from "../../../../contexts/AdminStatisticsContext";
import { SPACE_LG } from "../../../../styles/spacing";
import { ChartTypeButtons } from "./ChartTypeButtons";

import { getChartConfiguration } from "../../chart-config"

export default function AdvancedStatistics({statType}) {

  const {
    adminStatisticsContext,
    updateAnalysesStatsTimePeriod,
    updateProjectStatsTimePeriod,
    updateSampleStatsTimePeriod,
    updateUserStatsTimePeriod
  } = useContext(AdminStatisticsContext);

  const [timePeriod, setTimePeriod] = useState(defaultTimePeriod);
  const [chartType, setChartType] = useState(defaultChartType);
  const [form] = Form.useForm();

  const components = {
    [chartTypes.BAR]: Bar,
    [chartTypes.COLUMN]: Column,
    [chartTypes.LINE]: Line,
    [chartTypes.PIE]: Pie
  };

  useEffect(() => {
    setChartType(defaultChartType);
    setTimePeriod(defaultTimePeriod);
    form.setFieldsValue({
      "time-period": defaultTimePeriod,
    });
  }, [statType]);

  function updateTimePeriod(currTimePeriod) {
    if(statType === statisticTypes.ANALYSES) {
      updateAnalysesStatsTimePeriod(timePeriod);
    } else if (statType === statisticTypes.PROJECTS) {
      updateProjectStatsTimePeriod(timePeriod);
    } else if (statType === statisticTypes.SAMPLES) {
      updateSampleStatsTimePeriod(timePeriod)
    } else if (statType === statisticTypes.USERS) {
      updateUserStatsTimePeriod(timePeriod);
    }
    setTimePeriod(currTimePeriod);
  }

  function displayChart() {
    const Component = components[chartType];
    return Component ? (
      <Component {...getChartConfiguration(chartType, statType, timePeriod, adminStatisticsContext.statistics)} />
    ) : null;
  }

  return (
    <div>
        <Form
          form={form}
          initialValues={{
            "time-period": defaultTimePeriod,
          }}
          style={{marginBottom: SPACE_LG}}
        >
          <TimePeriodSelect onChange={(e) => updateTimePeriod(e)} />
          <ChartTypeButtons onChange={(e) => setChartType(e.target.value)} value={chartType}/>
        </Form>
      {displayChart()}
    </div>
  );
}