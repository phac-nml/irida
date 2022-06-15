import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * @file API for handling activities
 */

const BASE_URL = setBaseUrl(`/ajax/activities`);

/**
 * Get a page of activities for a project
 *
 * @param {number} projectId - current project identifier
 * @param {number} page - page of activities requested
 * @returns {Promise<AxiosResponse<any>>}
 */
export function getProjectActivities({ projectId, page = 0 }) {
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
export function getUserActivities({ page = 0 }) {
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
export function getAllRecentActivities({ page = 0 }) {
  try {
    return axios.get(`${BASE_URL}/all?page=${page}`).then(({ data }) => data);
  } catch (e) {
    return Promise.reject(i18n("RecentActivity.loadError"));
  }
}
