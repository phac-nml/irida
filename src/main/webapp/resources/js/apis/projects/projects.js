/**
 * @file API the ProjectAjaxController
 */
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`ajax/projects`);

/**
 * Returns the projects on the current page of the projects table.
 * @param {Object} params
 * @returns {Promise<{}|T>}
 */
export async function getPagedProjectsForUser(params) {
  try {
    const { data } = await axios.post(
      `${URL}?admin=${window.location.href.includes("all")}
  `,
      params
    );
    return data;
  } catch (e) {
    return {};
  }
}

/**
 * Get a list of available project roles
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getProjectRoles() {
  return await axios.get(`${URL}/roles`).then(({ data }) => data);
}

/**
 * Get project info (name, permissions)
 * @param {number} projectId - identifier for a project
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getProjectInfo(projectId) {
  return axios.get(`${URL}/${projectId}/info`).then(({ data }) => data);
}
