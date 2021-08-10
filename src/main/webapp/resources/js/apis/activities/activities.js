import axios from "axios";

/**
 * @file API for handling activities
 */

const BASE_URL = `/ajax/activities`;

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
