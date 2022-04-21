/*
 * This file renders the Advanced Statistics component
 * which displays statistics in charts
 */

import { Bar, Column, Line, Pie } from "@ant-design/plots";
import { Card, Form, PageHeader, Spin } from "antd";
import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getUpdatedStatistics } from "../../../../apis/admin/admin";

import { SPACE_LG, SPACE_MD } from "../../../../styles/spacing";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { getChartConfiguration } from "../../chart-config";

import {
  chartTypes,
  defaultChartType,
  defaultTimePeriod,
  statisticTypes,
} from "../../statistics-constants";
import { ChartTypeButtons } from "./ChartTypeButtons";
import { TimePeriodSelect } from "./TimePeriodSelect";

export default function AdvancedStatistics() {
  const params = useParams();
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [chartType, setChartType] = useState(defaultChartType);
  const [data, setData] = useState([]);

  useEffect(() => {
    getUpdatedStatistics(params.statType, defaultTimePeriod).then(
      ({ statistics }) => {
        setData(statistics);
        setLoading(false);
      }
    );
  }, [params.statType]);

  const components = {
    [chartTypes.BAR]: Bar,
    [chartTypes.COLUMN]: Column,
    [chartTypes.LINE]: Line,
    [chartTypes.PIE]: Pie,
  };

  const TITLES = {
    [statisticTypes.ANALYSES]: i18n(
      "AdminPanelStatistics.advancedStatistics.titleAnalysesRan"
    ),
    [statisticTypes.PROJECTS]: i18n(
      "AdminPanelStatistics.advancedStatistics.titleProjectsCreated"
    ),
    [statisticTypes.SAMPLES]: i18n(
      "AdminPanelStatistics.advancedStatistics.titleSamplesCreated"
    ),
    [statisticTypes.USERS]: i18n(
      "AdminPanelStatistics.advancedStatistics.titleUsersCreated"
    ),
  };

  function updateTimePeriod(e) {
    setLoading(true);
    getUpdatedStatistics(params.statType, e.target.value).then(
      ({ statistics }) => {
        setData(statistics);
        setLoading(false);
      }
    );
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
        title={TITLES[params.statType]}
        onBack={() => navigate(setBaseUrl(`/admin`))}
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
          tip={i18n("AdminPanelStatistics.advancedStatistics.fetchingData")}
        >
          {displayChart()}
        </Spin>
      </Card>
    </>
  );
}
