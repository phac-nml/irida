import { Statistic } from "antd";
import { statisticTypes } from "../../pages/admin/statistics-constants";
import { get } from "../requests";
import {
  admin_statistics_route,
  admin_statistics_analyses_route,
  admin_statistics_projects_route,
  admin_statistics_samples_route,
  admin_statistics_users_route,
} from "../routes";

type Statistic = {
  key: string;
  value: number;
};
export interface Statistics {
  analysisStats: Statistic[];
  projectStats: Statistic[];
  sampleStats: Statistic[];
  userStats: Statistic[];
  usersLoggedIn: number;
}

/*
 * Get all the statistics for the admin panel
 */
export async function getAdminStatistics(
  timePeriod: number
): Promise<Statistics> {
  return await get(admin_statistics_route(), {
    params: { timePeriod },
  });
}

/*
 * Get updated stats for the stat type in time period
 */
export async function getUpdatedStatistics(
  type: string,
  timePeriod: number
): Promise<Statistics> {
  switch (type) {
    case statisticTypes.USERS:
      return getUpdatedAdminUserStatistics(timePeriod);
    case statisticTypes.SAMPLES:
      return getUpdatedAdminSampleStatistics(timePeriod);
    case statisticTypes.PROJECTS:
      return getUpdatedAdminProjectStatistics(timePeriod);
    case statisticTypes.ANALYSES:
      return getUpdatedAdminAnalysesStatistics(timePeriod);
    default:
      throw new Error(`Cannot fetch updates for ${type}`);
  }
}

/*
 * Get updated project statistics for the provided time period
 */
export async function getUpdatedAdminProjectStatistics(
  timePeriod: number
): Promise<Statistics> {
  return await get(admin_statistics_projects_route(), {
    params: {
      timePeriod,
    },
  });
}

/*
 * Get updated analyses statistics for the provided time period
 */
export async function getUpdatedAdminAnalysesStatistics(
  timePeriod: number
): Promise<Statistics> {
  return await get(admin_statistics_analyses_route(), {
    params: {
      timePeriod,
    },
  });
}

/*
 * Get updated sample statistics for the provided time period
 */
export async function getUpdatedAdminSampleStatistics(
  timePeriod: number
): Promise<Statistics> {
  return get(admin_statistics_samples_route(), {
    params: {
      timePeriod,
    },
  });
}

/*
 * Get updated user statistics for the provided time period
 */
export async function getUpdatedAdminUserStatistics(
  timePeriod: number
): Promise<Statistics> {
  return get(admin_statistics_users_route(), {
    params: {
      timePeriod,
    },
  });
}
