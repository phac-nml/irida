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
 * Get information about the current project
 * @param {number} projectId - identifier for a project
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getProjectDetails(projectId) {
  return axios.get(`${URL}/${projectId}/details`).then(({ data }) => data);
}

/**
 * Update an attribute on a project
 * @param {number} projectId - identifier for a project
 * @param {string} field - attribute to update
 * @param {string} value - new value of the attribute
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function updateProjectAttribute({ projectId, field, value }) {
  return axios.put(`${URL}/${projectId}/details/edit`, { field, value });
}
