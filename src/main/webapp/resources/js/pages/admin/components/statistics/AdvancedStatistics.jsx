/*
 * This file renders the Advanced Statistics component
 * which displays statistics in charts
 */

import React, { useEffect, useState } from "react";
import { Bar, Column, Line, Pie } from "@ant-design/charts";
import { Card, Form, PageHeader, Spin } from "antd";
import { TimePeriodSelect } from "./TimePeriodSelect";

import {
  chartTypes,
  defaultChartType,
  defaultTimePeriod,
  statisticTypes,
} from "../../statistics-constants";

import { SPACE_LG, SPACE_MD } from "../../../../styles/spacing";
import { ChartTypeButtons } from "./ChartTypeButtons";
import { getChartConfiguration } from "../../chart-config";
import { useNavigate } from "@reach/router";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { getUpdatedStatistics } from "../../../../apis/admin/admin";

export default function AdvancedStatistics({ statType }) {
  const [loading, setLoading] = useState(true);
  const [chartType, setChartType] = useState(defaultChartType);
  const [data, setData] = useState([]);

  const [form] = Form.useForm();
  const navigate = useNavigate();

  useEffect(() => {
    getUpdatedStatistics(statType, defaultTimePeriod).then(({ statistics }) => {
      setData(statistics);
      setLoading(false);
    });
  }, [statType]);

  const components = {
    [chartTypes.BAR]: Bar,
    [chartTypes.COLUMN]: Column,
    [chartTypes.LINE]: Line,
    [chartTypes.PIE]: Pie,
  };

  const TITLES = {
    [statisticTypes.ANALYSES]: "Number of Analyses Ran",
    [statisticTypes.PROJECTS]: "Number of Projects Created",
    [statisticTypes.SAMPLES]: "Number of Samples Created",
    [statisticTypes.USERS]: "Number of Users Created",
  };

  function updateTimePeriod(e) {
    setLoading(true);
    getUpdatedStatistics(statType, e.target.value).then(({ statistics }) => {
      setData(statistics);
      setLoading(false);
    });
  }

  function displayChart() {
    const Component = components[chartType];

    return Component ? (
      <Component {...getChartConfiguration(chartType, data)} />
    ) : null;
  }

  return (
    <>
      <PageHeader
        title={TITLES[statType]}
        onBack={() => navigate(setBaseUrl(`/admin/statistics`))}
      />
      <Card style={{ margin: SPACE_LG }}>
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            "time-period": defaultTimePeriod,
            "chart-type": defaultChartType,
          }}
          style={{ marginBottom: SPACE_MD }}
        >
          <TimePeriodSelect onChange={updateTimePeriod} />
          <ChartTypeButtons
            onChange={(e) => setChartType(e.target.value)}
            value={chartType}
          />
        </Form>
        <Spin
          spinning={loading}
          delay={500}
          tip={"Fetching data for updated time period"}
        >
          {displayChart()}
        </Spin>
      </Card>
    </>
  );
}
