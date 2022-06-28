import axios, { AxiosResponse } from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * @file API for handling activities
 */

const BASE_URL = setBaseUrl(`/ajax/activities`);

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
export function getProjectActivities(projectId: number, page = 0): Promise<Activities> {
  try {
    return axios
      .get(`${BASE_URL}/project?projectId=${projectId}&page=${page}`)
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
    return axios.get(`${BASE_URL}/user?page=${page}`).then(({ data }) => data);
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
    return axios.get(`${BASE_URL}/all?page=${page}`).then(({ data }) => data);
  } catch (e) {
    return Promise.reject(i18n("RecentActivity.loadError"));
  }
}
