/*
 * This file gets the usage statistics and sets the global state
 * for the admin statistics page
 */

import React, { useEffect, useState } from "react";
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
    analysesStats: null,
    projectStats: null,
    sampleStats: null,
    userStats: null
  },
  basicStats : {
    analysesRan: 0,
    projectsCreated: 0,
    samplesCreated: 0,
    usersLoggedIn: 0
  }
};

const AdminStatisticsContext = React.createContext(initialContext);

function AdminStatisticsProvider(props) {
  const [adminStatisticsContext, setAdminStatisticsContext] = useState(initialContext);

  useEffect(() => {
    // On load get the basic for the default time period
    getAdminStatistics(defaultTimePeriod).then(basicStats => {
      setAdminStatisticsContext(adminStatisticsContext => {
        return {...adminStatisticsContext, basicStats: basicStats};
      });
    }).catch((message) => {
      notification.error({ message });
    });
  }, []);

  // Get updated project usage stats for the selected time period
  function updateProjectStatsTimePeriod(timePeriod) {
    getUpdatedAdminProjectStatistics(timePeriod).then(({projectStats}) => {
      if(projectStats !== null) {
        setAdminStatisticsContext(adminStatisticsContext => {
          return {...adminStatisticsContext,
            statistics: {
              ...adminStatisticsContext.statistics,
              projectStats: projectStats
            }
          };
        });
      }
    }).catch((message) => {
      notification.error({ message });
    });
  }

  // Get updated user usage stats for the selected time period
  function updateUserStatsTimePeriod(timePeriod) {
    getUpdatedAdminUserStatistics(timePeriod).then(({userStats}) => {
      if(userStats !== null) {
        setAdminStatisticsContext(adminStatisticsContext => {
          return {...adminStatisticsContext,
            statistics: {
              ...adminStatisticsContext.statistics,
              userStats: userStats
            }
          };
        });
      }
    }).catch((message) => {
      notification.error({ message });
    });
  }

  // Get updated analyses usage stats for the selected time period
  function updateAnalysesStatsTimePeriod(timePeriod) {
    getUpdatedAdminAnalysesStatistics(timePeriod).then(({analysesStats}) => {
      if(analysesStats !== null) {
        setAdminStatisticsContext(adminStatisticsContext => {
          return {...adminStatisticsContext,
            statistics: {
              ...adminStatisticsContext.statistics,
              analysesStats: analysesStats
            }
          };
        });
      }
    }).catch((message) => {
      notification.error({ message });
    });
  }

  // Get updated sample usage stats for the selected time period
  function updateSampleStatsTimePeriod(timePeriod) {
    getUpdatedAdminSampleStatistics(timePeriod).then(({sampleStats}) => {
      if(sampleStats !== null) {
        setAdminStatisticsContext(adminStatisticsContext => {
          return {...adminStatisticsContext,
            statistics: {
              ...adminStatisticsContext.statistics,
              sampleStats: sampleStats
            }
          };
        });
      }
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

    // The configuration required to display a chart
    const chartConfig = {
      title: { visible: true, text: chartTitle },
      forceFit: true,
      height: 800,
      data: data !== null ? data : [{key:"", value:""}],
      padding: 'auto',
      xField: isBarChartType ? 'value' : 'key',
      yField: isBarChartType ? 'key' : 'value',
      meta: { key: { alias: 'Time Period' }, value: { alias: chartAxisAlias } },
      angleField:"value",
      label: {
        visible: data !== null ? (isPieDonutChartType ? false : true) : false,
        position: isBarChartType ? 'right' : 'middle',
        adjustColor: true,
        style: { fill: '#0D0E68', fontSize: 12, fontWeight: 600, opacity: 0.3 },
      },
      colorField: "key",
      legend: {
        visible: data !== null  ? true : false,
        position: 'bottom-center',
      },
      statistic: {
        visible: data !== null ? true : false,
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
