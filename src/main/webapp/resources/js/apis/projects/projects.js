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

export async function getProjectDetails(projectId) {
  return axios.get(`${URL}/${projectId}/details`).then(({ data }) => data);
}
