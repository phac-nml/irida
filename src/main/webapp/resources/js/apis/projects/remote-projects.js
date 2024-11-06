import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/remote-projects`);

/**
 * Update an attribute on a remote project
 * @param {number} projectId - identifier for a project
 * @param {object} {} - object which contains frequency,
 * forceSync, and/or changeUser values to update
 * @returns {Promise<{error: *}>}
 */
export async function updateRemoteProjectSyncSettings(
  projectId,
  { forceSync, markSync, changeUser, projectSyncFrequency }
) {
  return await axios
    .post(`${BASE_URL}/${projectId}/settings/sync`, {
      forceSync,
      markSync,
      changeUser,
      projectSyncFrequency,
    })
    .then(({ data }) => data)
    .catch((error) => {
      throw new Error(error.response.data.error);
    });
}

/**
 * Get remote project sync settings
 * @param {number} projectId - identifier for a project
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getRemoteProjectSyncSettings(projectId) {
  return axios
    .get(`${BASE_URL}/${projectId}/settings/remote-settings`)
    .then(({ data }) => data)
    .catch((error) => {
      throw new Error(error.response.data.error);
    });
}
