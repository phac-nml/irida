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

export function getUserGroupRoles() {
  return axios.get(`${BASE_URL}/roles`).then(({ data }) => data);
}
