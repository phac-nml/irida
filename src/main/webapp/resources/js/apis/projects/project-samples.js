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
export async function getPagedProjectSamples(projectId, body) {
  return await axios.post(`${BASE_URL}${projectId}`, body);
}

/**
 * Get get minimal information for all samples in a project.
 * @param {number} projectId - identifier for a project
 * @param {object} filters - current table filters
 * @returns {Promise<*>}
 */
export async function getAllSampleIds(projectId, filters) {
  return await axios.post(`${BASE_URL}${projectId}/ids`, { filters });
}
