import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/projects/${window.project.id}/user-groups`);

/**
 * Remove a user group from a project
 * @param {number} groupId Identifier of user group to remove
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function removeUserGroupFromProject({ groupId }) {
  return axios
    .delete(`${BASE_URL}?groupId=${groupId}`)
    .then(({ data }) => data);
}

/**
 * Get a filtered list of user groups that are not on the current project
 * @param {string} query to filter the user groups by
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getAvailableGroupsForProject(query) {
  return axios
    .get(`${BASE_URL}/available?query=${query}`)
    .then(({ data }) => data);
}

/**
 * Add a user group to the current project
 * @param {number} groupId Identifier for the user group to add
 * @param {string} role for the user group on the project
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function addUserGroupToProject({ groupId, role }) {
  return axios
    .post(`${BASE_URL}/add`, { role, id: groupId })
    .then(({ data }) => data);
}

/**
 * Update the role of a user group on the current project
 * @param {number} groupId Identifier for the user group to add
 * @param {string} role for the user group on the project
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function updateUserGroupRoleOnProject({ id, role }) {
  return axios
    .put(`${BASE_URL}/role?id=${id}&role=${role}`)
    .then(({ data }) => data);
}
