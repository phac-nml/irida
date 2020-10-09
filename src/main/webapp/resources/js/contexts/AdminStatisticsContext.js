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
  1: i18n("AdminPanelStatistics.day"),
  7: i18n("AdminPanelStatistics.week"),
  14: i18n("AdminPanelStatistics.twoWeeks"),
  30: i18n("AdminPanelStatistics.month"),
  90: i18n("AdminPanelStatistics.quarter"),
  365: i18n("AdminPanelStatistics.year"),
  730: i18n("AdminPanelStatistics.twoYears"),
  1825: i18n("AdminPanelStatistics.fiveYears"),
  3650: i18n("AdminPanelStatistics.tenYears")
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
    usersCreated: 0,
    usersLoggedIn: 0
  }
};

const AdminStatisticsContext = React.createContext(initialContext);

function AdminStatisticsProvider(props) {
  const [adminStatisticsContext, setAdminStatisticsContext] = useState(initialContext);

  useEffect(() => {
    // On load get the basic usage stats for the default time period
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
    }).catch(({message}) => {
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
    }).catch(({message}) => {
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
    }).catch(({message}) => {
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
    }).catch(({message})  => {
      notification.error({ message });
    });
  }

  return (
    <AdminStatisticsContext.Provider
      value={{
        adminStatisticsContext,
        updateProjectStatsTimePeriod,
        updateUserStatsTimePeriod,
        updateAnalysesStatsTimePeriod,
        updateSampleStatsTimePeriod
      }}
    >
      {props.children}
    </AdminStatisticsContext.Provider>
  );
}
export { AdminStatisticsContext, AdminStatisticsProvider };
