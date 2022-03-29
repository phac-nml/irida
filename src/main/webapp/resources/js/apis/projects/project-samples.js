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

export function getAllSampleIds(projectId, filters) {
  return axios.post(`${BASE_URL}${projectId}/ids`, { filters });
}
