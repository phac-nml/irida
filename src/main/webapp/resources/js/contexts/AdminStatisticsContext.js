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

export const chartTypes = {
  BAR: "bar",
  COLUMN: "column",
  DONUT: "donut",
  LINE: "line",
  PIE: "pie"
}

export const statisticTypes = {
  ANALYSES: "analyses",
  PROJECTS: "projects",
  SAMPLES: "samples",
  USERS: "users"
}

// Default is to display statistics for the last week
export const defaultTimePeriod = 7;

export const defaultChartType = chartTypes.BAR;

const initialContext = {
  statistics: {
    analysesStats: [
      { key: '2019', value: 1607 },
      { key: '2018', value: 801 },
      { key: '2017', value: 421 },
      { key: '2016', value: 221 },
      { key: '2015', value: 145 },
      { key: '2014', value: 61 },
      { key: '2013', value: 52 },
      { key: '2012', value: 38 }
    ],
    projectStats: [{}],
    sampleStats: [{}],
    userStats: [{}]
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
  //   getAdminStatistics(defaultTimePeriod).then(res => {
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
   * Gets the config required for the chart
   * @param chartType - The type of chart (bar, column, line, or pie)
   * @param data - The data for the chart
   * @param statsType - The type of statistics (projects, analyses, samples, users)
   * @param timePeriod - The time period for the statistics
   */
  function getChartConfig(chartType, statsType, timePeriod)
  {
    let data = null;
    const timePeriodText = timePeriodMap[timePeriod];
    const isBarChartType = chartType === chartTypes.BAR;
    const isPieDonutChartType = chartType === chartTypes.DONUT || chartType === chartTypes.PIE;

    let chartTitle = "";
    let chartAxisAlias = "";

    if(statsType === statisticTypes.ANALYSES) {
      chartTitle = `Number of analyses run in past ${timePeriodText}`;
      chartAxisAlias = '# of Analyses';
      data = adminStatisticsContext.statistics.analysesStats;
    } else if (statsType === statisticTypes.PROJECTS) {
      chartTitle = `Number of projects created in past ${timePeriodText}`;
      chartAxisAlias ='# of Projects';
    } else if (statsType === statisticTypes.SAMPLES)
    {
      chartTitle = `Number of samples created in past ${timePeriodText}`;
      chartAxisAlias = '# of Samples';
    } else if (statsType === statisticTypes.USERS) {
      chartTitle =`Number of users logged on in past ${timePeriodText}`;
      chartAxisAlias = '# of Users';
    }

    // Some charts for examples Column need the data reversed so that it is
    // displayed in the correct order
    const revData = data !== null ? [...data].reverse() : null;

    // The configuration required to display a chart
    const chartConfig = {
      title: { visible: true, text: chartTitle },
      forceFit: true,
      data: data !== null || revData !== null ? (isBarChartType ? data : revData) : [{key:"", value:""}],
      padding: 'auto',
      xField: isBarChartType ? 'value' : 'key',
      yField: isBarChartType ? 'key' : 'value',
      meta: { key: { alias: 'Time Period' }, value: { alias: chartAxisAlias } },
      angleField:"value",
      label: {
        visible: data !== null || revData !== null ? (isPieDonutChartType ? false : true) : false,
        position: isBarChartType ? 'right' : 'middle',
        adjustColor: true,
        style: { fill: '#0D0E68', fontSize: 12, fontWeight: 600, opacity: 0.3 },
      },
      colorField: "key",
      legend: {
        visible: data !== null || revData !== null ? true : false,
        position: 'bottom-center',
      },
      statistic: {
        visible: data !== null || revData !== null ? true : false,
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
