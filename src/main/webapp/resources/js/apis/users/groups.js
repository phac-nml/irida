import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl("/ajax/user-groups");

/**
 * Delete a specific user group
 *
 * @param {number} id - identiifer for a user group.
 * @returns {Promise<AxiosResponse<any>>}
 */
export function deleteUserGroup(id) {
  return axios.delete(`${BASE_URL}?id=${id}`).then(({ data }) => data);
}

/**
 * Get details about a specific user group
 *
 * @param {number} id for a user group
 * @returns {Promise<AxiosResponse<any>>}
 */
export function getUserGroupDetails(id) {
  return axios.get(`${BASE_URL}/${id}`).then(({ data }) => data);
}

/**
 * Update the User Group fields
 * @param {number} id for the user groupd
 * @param {string} field to update
 * @param {string} value to set
 * @returns {Promise<AxiosResponse<any>>}
 */
export function updateUserGroupDetails({ id, field, value }) {
  return axios.put(`${BASE_URL}/${id}/update `, {
    field,
    value,
  });
}

/**
 * Get key value pairs of all user group roles with their translations
 * @returns {Promise<AxiosResponse<any>>}
 */
export function getUserGroupRoles() {
  return axios.get(`${BASE_URL}/roles`).then(({ data }) => data);
}

/**
 * Update a user group members role in the group
 * @param {number} groupId identifier for a user group
 * @param {number} userId identifier for a user
 * @param {string} role role to update the user to
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function updateUserRoleOnUserGroups({ groupId, userId, role }) {
  try {
    const { data } = await axios.put(
      `${BASE_URL}/${groupId}/member/role?userId=${userId}&role=${role}`
    );
    return Promise.resolve(data);
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

/**
 * Get a list of users that are not currently on the project, filtered
 * by a search query
 * @param {number} id identifier for the user group
 * @param {string} query to filter the users by
 * @returns {Promise<AxiosResponse<any>>}
 */
export function getAvailableUsersForUserGroup({ id, query }) {
  return axios
    .get(`${BASE_URL}/${id}/available?query=${query}`)
    .then(({ data }) => data);
}

/**
 * Add a new user to a user group
 * @param {number} groupId identifier for the user group
 * @param {number} userId identifier for the user to add
 * @param {string} role role to add to the user
 * @returns {Promise<AxiosResponse<any>>}
 */
export function addMemberToUserGroup({ groupId, userId, role }) {
  const params = new URLSearchParams();
  params.append("userId", userId);
  params.append("role", role);
  return axios
    .post(`${BASE_URL}/${groupId}/add`, params)
    .then(({ data }) => data);
}

/**
 * Remove a member from the user group
 * @param {number} groupId identifier for the user group
 * @param {number} userId identifier for the user to remove
 * @returns {Promise<AxiosResponse<any>>}
 */
export function removeMemberFromUserGroup({ groupId, userId }) {
  return axios
    .delete(`${BASE_URL}/${groupId}/remove?userId=${userId}`)
    .then(({ data }) => data);
}

/**
 * Get a list of projects on a user group
 * @param {number} groupId identifier for a user group
 * @returns {Promise<AxiosResponse<any>>}
 */
export function getProjectsForUserGroup(groupId) {
  return axios.get(`${BASE_URL}/${groupId}/projects`).then(({ data }) => data);
}

/**
 * Create a new user groups
 * @param {string} name
 * @param {string} description
 * @returns {Promise<AxiosResponse<any> | void>}
 */
export function createUserGroup({ name, description }) {
  return axios
    .post(`${BASE_URL}/create`, {
      name,
      description,
    })
    .then(({ data }) => data);
}
