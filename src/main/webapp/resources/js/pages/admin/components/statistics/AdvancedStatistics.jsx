/*
 * This file renders the Advanced Statistics component
 * which displays statistics in charts
 */

import React, { useContext, useEffect, useState } from "react";
import { Bar, Column, Donut, Line, Pie } from "@ant-design/charts";
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

export default function AdvancedStatistics({statType}) {

  const {
    adminStatisticsContext,
    updateAnalysesStatsTimePeriod,
    updateProjectStatsTimePeriod,
    updateSampleStatsTimePeriod,
    updateUserStatsTimePeriod,
    getChartConfig
  } = useContext(AdminStatisticsContext);

  const [timePeriod, setTimePeriod] = useState(defaultTimePeriod);
  const [chartType, setChartType] = useState(defaultChartType);
  const [form] = Form.useForm();

  useEffect(() => {
    setChartType(defaultChartType);
    setTimePeriod(defaultTimePeriod);
    form.setFieldsValue({
      "time-period": defaultTimePeriod,
    });
  }, [statType]);

  function updateTimePeriod(currTimePeriod) {
    if(statType === statisticTypes.ANALYSES) {
      updateAnalysesStatsTimePeriod(currTimePeriod);
    } else if (statType === statisticTypes.PROJECTS) {
      updateProjectStatsTimePeriod(currTimePeriod);
    } else if (statType === statisticTypes.SAMPLES) {
      updateSampleStatsTimePeriod(currTimePeriod)
    } else if (statType === statisticTypes.USERS) {
      updateUserStatsTimePeriod(currTimePeriod);
    }
    setTimePeriod(currTimePeriod);
  }

  function displayChart() {
    if(chartType === chartTypes.BAR) {
      return <Bar {...getChartConfig(chartType, statType, timePeriod)} ></Bar>;
    } else if (chartType === chartTypes.COLUMN) {
      return <Column {...getChartConfig(chartType, statType, timePeriod)} ></Column>;
    } else if (chartType === chartTypes.LINE) {
      return <Line {...getChartConfig(chartType, statType, timePeriod)} ></Line>;
    } else if (chartType === chartTypes.PIE) {
      return <Pie {...getChartConfig(chartType, statType, timePeriod)} ></Pie>;
    } else if (chartType === chartTypes.DONUT) {
      return <Donut {...getChartConfig(chartType, statType, timePeriod)}  />
    } else {
      return null;
    }
  }

  return (
    <div style={{marginBottom: SPACE_LG}}>
      <Form
        form={form}
        initialValues={{
          "time-period": defaultTimePeriod,
        }}
      >
        <TimePeriodSelect onChange={(e) => updateTimePeriod(e)} />
        <ChartTypeButtons onChange={(e) => setChartType(e.target.value)} value={chartType}/>
      </Form>
      {displayChart()}
    </div>
  );
}