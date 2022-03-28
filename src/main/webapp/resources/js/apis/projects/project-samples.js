import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/project-samples/`);

/**
 * Get a paged list of project samples
 *
 * @param {number} projectId Project identifier for the current project
 * @param {object} body paging, ordering and sorting information
 * @returns Axios promise
 */
export function getPagedProjectSamples(projectId, body) {
  return axios.post(`${BASE_URL}${projectId}`, body);
}

/**
 * Get a list of all the associated projects for the current project
 *
 * @param {number} projectId Project identifier for the current project
 * @returns Axios promise
 */
export function getAssociatedProjectForProject(projectId) {
  return axios.get(`${BASE_URL}${projectId}/associated`);
}
