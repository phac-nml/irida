import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/projects/groups`);

/**
 * Remove a user group from a project
 *
 * @param {number} projectId Identifier for the current project
 * @param {number} groupId Identifier of user group to remove
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function removeUserGroupFromProject({ projectId, groupId }) {
  const params = new URLSearchParams({ projectId, groupId });
  try {
    const { data } = await axios.delete(`${BASE_URL}?${params.toString()}`);
    return Promise.resolve(data);
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

/**
 * Get a filtered list of user groups that are not on the current project
 *
 * @param {number} projectId Identifier for the current project
 * @param {string} query to filter the user groups by
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getAvailableGroupsForProject({ projectId, query }) {
  const params = new URLSearchParams({ projectId, query });
  try {
    const { data } = await axios.get(
      `${BASE_URL}/available?${params.toString()}`
    );
    return Promise.resolve(data);
    s;
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

/**
 * Add a user group to the current project
 *
 * @param {number} projectId Identifier for the current project
 * @param {number} groupId Identifier for the user group to add
 * @param {string} role for the user group on the project
 * @param {string} metadataRole for the user group on the project
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function addUserGroupToProject({
  projectId,
  groupId,
  role,
  metadataRole,
}) {
  try {
    const { data } = await axios.post(
      `${BASE_URL}/add?projectId=${projectId}`,
      { projectRole: role, id: groupId, metadataRole }
    );
    return Promise.resolve(data);
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

/**
 * Update the role of a user group on the current project
 *
 * @param {number} projectId Identifier for the current project
 * @param {number} groupId Identifier for the user group to add
 * @param {string} projectRole for the user group on the project
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function updateUserGroupProjectRole({
  projectId,
  id,
  projectRole = "",
}) {
  const params = new URLSearchParams({
    projectId,
    id,
    projectRole,
  });
  try {
    const { data } = await axios.put(`${BASE_URL}/role?${params.toString()}`);
    return Promise.resolve(data);
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

/**
 * Update the metadata role of a user group on the current project
 *
 * @param {number} projectId Identifier for the current project
 * @param {number} groupId Identifier for the user group to add
 * @param {string} metadataRole for the user group metadata access on the project
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function updateUserGroupProjectMetadataRole({
 projectId,
 id,
 metadataRole = "",
}) {
  const params = new URLSearchParams({
    projectId,
    id,
    metadataRole,
  });
  try {
    const { data } = await axios.put(`${BASE_URL}/metadata-role?${params.toString()}`);
    return Promise.resolve(data);
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}
