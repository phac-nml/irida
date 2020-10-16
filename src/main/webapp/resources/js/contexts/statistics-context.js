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
import { defaultTimePeriod, statisticTypes } from "../pages/admin/statistics-constants";

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

  function getUpdatedStatsForStatType(statType, timePeriod) {
    if (statType === statisticTypes.ANALYSES) {
      updateAnalysesStatsTimePeriod(timePeriod);
    } else if (statType === statisticTypes.PROJECTS) {
      updateProjectStatsTimePeriod(timePeriod);
    } else if (statType === statisticTypes.SAMPLES) {
      updateSampleStatsTimePeriod(timePeriod);
    } else if (statType === statisticTypes.USERS) {
      updateUserStatsTimePeriod(timePeriod);
    }
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
        updateSampleStatsTimePeriod,
        getUpdatedStatsForStatType
      }}
    >
      {props.children}
    </AdminStatisticsContext.Provider>
  );
}
export { AdminStatisticsContext, AdminStatisticsProvider };
