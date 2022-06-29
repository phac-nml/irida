import axios, {AxiosResponse} from "axios";
import {activities_project_route, activities_recent_route, activities_user_route,} from "../routes";

/**
 * @file API for handling activities
 */

export interface ActivitiesResponse extends AxiosResponse {
  data: Activities;
}

export interface Activities {
  total: number;
  content: Activity[];
}

export interface Activity {
  id: number;
  type: string;
  description: string;
  date: Date;
  items: ActivityItem[];
}

export interface ActivityItem {
  href: string;
  label: string;
}

/**
 * Get a page of activities for a project
 *
 * @param projectId - current project identifier
 * @param page - page of activities requested
 */
export function getProjectActivities(
  projectId: number,
  page = 0
): Promise<Activities> {
  try {
    return axios
      .get(
        activities_project_route(undefined, {
          projectId: `${projectId}`,
          page: `${page}`,
        })
      )
      .then(({ data }) => data);
  } catch (e) {
    return Promise.reject(i18n("ProjectActivity.error"));
  }
}

/**
 * Get a page of recent activities for all of user's projects
 *
 * @param page - page of activities requested
 */
export function getUserActivities(page = 0): Promise<Activities> {
  try {
    return axios
      .get(
        activities_user_route(undefined, {
          page: `${page}`,
        })
      )
      .then(({ data }) => data);
  } catch (e) {
    return Promise.reject(i18n("RecentActivity.loadError"));
  }
}

/**
 * Get a page of recent activities for all projects
 *
 * @param page - page of activities requested
 */
export function getAllRecentActivities(page = 0): Promise<Activities> {
  try {
    return axios
      .get(activities_recent_route(undefined, { page: `${page}` }))
      .then(({ data }) => data);
  } catch (e) {
    return Promise.reject(i18n("RecentActivity.loadError"));
  }
}
