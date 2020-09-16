import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/remote-projects`);

/**
 * Update an attribute on a remote project
 * @param {number} projectId - identifier for a project
 * @param {object} {} - object which contains frequency,
 * forceSync, and/or changeUser values to update
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function updateRemoteProjectSyncSettings(projectId, {forceSync, changeUser, projectSyncFrequency}) {
  try {
    const { data } = await axios.post(
      `${BASE_URL}/${projectId}/settings/sync`,
      {forceSync, changeUser, projectSyncFrequency}
    );
    return data;
  } catch (e) {
    return {};
  }
}

/**
 * Get remote project sync settings
 * @param {number} projectId - identifier for a project
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getRemoteProjectSyncSettings(projectId) {
  return axios.get(`${BASE_URL}/${projectId}/settings/remote-settings`).then(({ data }) => data);
}