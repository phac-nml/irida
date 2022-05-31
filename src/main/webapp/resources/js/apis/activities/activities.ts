import axios, { AxiosResponse } from "axios";

/**
 * @file API for handling activities
 */

const BASE_URL = `/ajax/activities`;

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
 * @param {number} projectId - current project identifier
 * @param {number} page - page of activities requested
 * @returns {Promise<AxiosResponse<any>>}
 */
export function getProjectActivities(projectId: number, page: number = 0): Promise<Activities> {
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
 * @param {number} page - page of activities requested
 * @returns {Promise<AxiosResponse<any>>}
 */
export function getUserActivities(page: number = 0): Promise<Activities> {
  try {
    return axios.get(`${BASE_URL}/user?page=${page}`).then(({ data }) => data);
  } catch (e) {
    return Promise.reject(i18n("RecentActivity.loadError"));
  }
}

/**
 * Get a page of recent activities for all projects
 *
 * @param {number} page - page of activities requested
 * @returns {Promise<AxiosResponse<any>>}
 */
export function getAllRecentActivities(page: number = 0): Promise<Activities> {
  try {
    return axios.get(`${BASE_URL}/all?page=${page}`).then(({ data }) => data);
  } catch (e) {
    return Promise.reject(i18n("RecentActivity.loadError"));
  }
}
