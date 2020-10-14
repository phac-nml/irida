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
  PIE: "pie",
  TINYCOLUMN: "tinyColumn"
}

export const statisticTypes = {
  ANALYSES: "analyses",
  PROJECTS: "projects",
  SAMPLES: "samples",
  USERS: "users"
}

// Default is to display statistics for the last week
export const defaultTimePeriod = 7;

export const defaultChartType = chartTypes.COLUMN;

const initialContext = {
  statistics: {
    analysesStats: [
      {key: "10/04", value: 10},
      {key: "10/05", value: 12},
      {key: "10/06", value: 16},
      {key: "10/07", value: 30},
      {key: "10/08", value: 100},
      {key: "10/09", value: 350},
      {key: "10/10", value: 999},
    ],
    projectStats: [
      {key: "10/04", value: 20},
      {key: "10/05", value: 12},
      {key: "10/06", value: 16},
      {key: "10/07", value: 30},
      {key: "10/08", value: 100},
      {key: "10/09", value: 350},
      {key: "10/10", value: 999},
    ],
    sampleStats: [
      {key: "10/04", value: 30},
      {key: "10/05", value: 12},
      {key: "10/06", value: 16},
      {key: "10/07", value: 30},
      {key: "10/08", value: 100},
      {key: "10/09", value: 350},
      {key: "10/10", value: 999},
    ],
    userStats: [
      {key: "10/04", value: 40},
      {key: "10/05", value: 12},
      {key: "10/06", value: 16},
      {key: "10/07", value: 30},
      {key: "10/08", value: 100},
      {key: "10/09", value: 350},
      {key: "10/10", value: 999},
    ]
  },
  basicStats : {
    analysesRun: 0,
    projectsCreated: 0,
    samplesCreated: 0,
    usersCreated: 0,
    usersLoggedIn: 0
  }
};

const AdminStatisticsContext = React.createContext(initialContext);

function AdminStatisticsProvider(props) {
  const [adminStatisticsContext, setAdminStatisticsContext] = useState(initialContext);

  // Get basic usage stats for the time period
  useEffect(() => {
    getAdminStatistics(defaultTimePeriod).then(res => {
      console.log(res);
    }).catch(({message}) => {
      notification.error({ message });
    });
  }, []);

  // Get updated project usage stats for the selected time period
  function updateProjectStatsTimePeriod(timePeriod) {
    getUpdatedAdminProjectStatistics(timePeriod).then(res => {
      console.log(res);
    }).catch(({message}) => {
      notification.error({ message });
    });
  }

  // Get updated user usage stats for the selected time period
  function updateUserStatsTimePeriod(timePeriod) {
    getUpdatedAdminUserStatistics(timePeriod).then(res => {
      console.log(res);
    }).catch(({message}) => {
      notification.error({ message });
    });
  }

  // Get updated analyses usage stats for the selected time period
  function updateAnalysesStatsTimePeriod(timePeriod) {
    getUpdatedAdminAnalysesStatistics(timePeriod).then(res => {
      console.log(res);
    }).catch(({message}) => {
      notification.error({ message });
    });
  }

  // Get updated sample usage stats for the selected time period
  function updateSampleStatsTimePeriod(timePeriod) {
    getUpdatedAdminSampleStatistics(timePeriod).then(res => {
      console.log(res);
    }).catch(({message}) => {
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
