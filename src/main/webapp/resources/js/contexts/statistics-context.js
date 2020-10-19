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
    analysesStats: [{}],
    projectStats: [{}],
    sampleStats: [{}],
    userStats: [{}],
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
    // On load get the basic stats for the default time period
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
