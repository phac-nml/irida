/*
 * This file renders the Advanced Statistics component
 * which displays statistics in charts
 */

import React, { useContext, useRef, useState } from "react";
import { Bar, Column, Donut, Line, Pie } from "@ant-design/charts";
import { Form } from "antd";
import { TimePeriodSelect } from "./TimePeriodSelect";
import {
  AdminStatisticsContext,
  defaultTimePeriod,
  defaultChartType
} from "../../../../contexts/AdminStatisticsContext";
import { SPACE_LG } from "../../../../styles/spacing";
import { ChartTypeButtons } from "./ChartTypeButtons";

export default function AdvancedStatistics({statType}) {
  const ref = useRef();

  const {
    adminStatisticsContext,
    updateAnalysesStatsTimePeriod,
    updateProjectStatsTimePeriod,
    updateSampleStatsTimePeriod,
    updateUserStatsTimePeriod,
    getChartConfig
  } = useContext(AdminStatisticsContext);

  const [ timePeriod, setTimePeriod ] = useState(defaultTimePeriod);
  const [ chartType, setChartType ] = useState(defaultChartType);


  function updateTimePeriod(currTimePeriod) {
    if(statType === "analyses") {
      updateAnalysesStatsTimePeriod(timePeriod);
    } else if (statType === "projects") {
      updateProjectStatsTimePeriod(timePeriod);
    } else if (statType === "samples") {
      updateSampleStatsTimePeriod(timePeriod)
    } else if (statType === "users") {
      updateUserStatsTimePeriod(timePeriod);
    }
    setTimePeriod(currTimePeriod);
  }

  const data = [
    { time: '2019', number: 1607 },
    { time: '2018', number: 801 },
    { time: '2017', number: 421 },
    { time: '2016', number: 221 },
    { time: '2015', number: 145 },
    { time: '2014', number: 61 },
    { time: '2013', number: 52 },
    { time: '2012', number: 38 },
  ];

  function displayChart() {
    let currentChart = null;
    if(chartType==="bar") {
      currentChart = <Bar {...getChartConfig(chartType, data, statType, timePeriod)} chartRef={ref}></Bar>;
    } else if (chartType === "column") {
      currentChart = <Column {...getChartConfig(chartType, data, statType, timePeriod)} chartRef={ref}></Column>;
    } else if (chartType === "line") {
      currentChart = <Line {...getChartConfig(chartType, data, statType, timePeriod)} chartRef={ref}></Line>;
    } else if (chartType === "pie") {
      currentChart = <Pie {...getChartConfig(chartType, data, statType, timePeriod)} chartRef={ref}></Pie>;
    } else if (chartType === "donut") {
      currentChart = <Donut {...getChartConfig(chartType, data, statType, timePeriod)} chartRef={ref} />
    }
    return currentChart;
  }

  return (
    <div style={{marginBottom: SPACE_LG}}>
      <Form
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