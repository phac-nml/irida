/*
 * This file gets the usage statistics and sets the global state
 * for the admin statistics page
 */

import React, { useState } from "react";
import { notification } from "antd";
import {
  getAdminStatistics,
  getUpdatedAdminAnalysesStatistics,
  getUpdatedAdminProjectStatistics,
  getUpdatedAdminSampleStatistics,
  getUpdatedAdminUserStatistics
} from "../apis/admin/admin";

// Default is to display statistics for the last week
export const defaultTimePeriod = 7;

export const defaultChartType = "bar";

export const timePeriodMap = {
  1: "day",
  7: "week",
  14: "2 weeks",
  30: "month",
  90: "quarter",
  365: "year",
  730: "2 years",
  1825: "5 years",
  3650: "10 years"
};

const initialContext = {
  statistics: {
    analysesStats: {},
    projectStats: {},
    sampleStats: {},
    userStats: {}
  },
  basicStats : {
    analysesRun: 25,
    projectsCreated: 64,
    samplesCreated: 128,
    usersLoggedIn: 12
  }
};

const AdminStatisticsContext = React.createContext(initialContext);

function AdminStatisticsProvider(props) {
  const [adminStatisticsContext, setAdminStatisticsContext] = useState(initialContext);

  // On load get the usage statistics for the default time period
  // useEffect(() => {
  //   getAdminStatistics().then(res => {
  //     console.log(res);
  //     setAdminStatisticsContext(res);
  //   }).catch((message) => {
  //     notification.error({ message });
  //   });
  // }, []);

  // Get updated project usage stats for the selected time period
  function updateProjectStatsTimePeriod(timePeriod) {
    getUpdatedAdminProjectStatistics(timePeriod).then(res => {
      console.log(res);
    }).catch((message) => {
      notification.error({ message });
    });
  }

  // Get updated user usage stats for the selected time period
  function updateUserStatsTimePeriod(timePeriod) {
    getUpdatedAdminUserStatistics(timePeriod).then(res => {
      console.log(res);
    }).catch((message) => {
      notification.error({ message });
    });
  }

  // Get updated analyses usage stats for the selected time period
  function updateAnalysesStatsTimePeriod(timePeriod) {
    getUpdatedAdminAnalysesStatistics(timePeriod).then(res => {
      console.log(res);
    }).catch((message) => {
      notification.error({ message });
    });
  }

  // Get updated sample usage stats for the selected time period
  function updateSampleStatsTimePeriod(timePeriod) {
    getUpdatedAdminSampleStatistics(timePeriod).then(res => {
      console.log(res);
    }).catch((message) => {
      notification.error({ message });
    });
  }

  /*
   * @param chartType - The type of chart (bar, column, line, or pie)
   * @param data - The data for the chart
   * @param statsType - The type of statistics (projects, analyses, samples, users)
   * @param timePeriod - The time period for the statistics
   */
  function getChartConfig(chartType, data, statsType, timePeriod)
  {
    const revData =  [...data].reverse();
    const timePeriodText = timePeriodMap[timePeriod];

    const chartTitle = statsType === "analyses" ? `Number of analyses run in past ${timePeriodText}` : statsType === "projects" ? `Number of projects created in past ${timePeriodText}` :
    statsType === "users" ? `Number of users logged on in past ${timePeriodText}` : `Number of samples created in past ${timePeriodText}` ;

    const chartAxisAlias = statsType === "analyses" ? '# of Analyses' : statsType === "projects" ? '# of Projects' :
      statsType === "users" ? '# of Users' : '# of Samples' ;

    const chartConfig = {
      title: { visible: true, text: chartTitle },
      forceFit: true,
      data: chartType === "bar" ? data : revData,
      padding: 'auto',
      xField: chartType === "bar" ? 'number' : 'time',
      yField: chartType === "bar" ? 'time' : 'number',
      meta: { time: { alias: 'Year' }, number: { alias: chartAxisAlias } },
      angleField:"number",
      label: {
        visible: chartType === "pie" || chartType === "donut" ? false : true,
        position: chartType === "bar" ? 'right' : 'middle',
        adjustColor: true,
        style: { fill: '#0D0E68', fontSize: 12, fontWeight: 600, opacity: 0.3 },
      },
      colorField: "time",
      legend: {
        visible: true,
        position: 'bottom-center',
      },
      statistic: {
        visible: true,
        content: {
          value: "",
          name: '',
        },
      },
    };

    return chartConfig;
  }

  return (
    <AdminStatisticsContext.Provider
      value={{
        adminStatisticsContext,
        updateProjectStatsTimePeriod,
        updateUserStatsTimePeriod,
        updateAnalysesStatsTimePeriod,
        updateSampleStatsTimePeriod,
        getChartConfig

      }}
    >
      {props.children}
    </AdminStatisticsContext.Provider>
  );
}
export { AdminStatisticsContext, AdminStatisticsProvider };
