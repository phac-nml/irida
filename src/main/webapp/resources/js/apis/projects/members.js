import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * @file API for handle server interactions for members of a project
 */

/**
 * Base url for server interaction for members, set with the proper servlet
 * context
 * @type {string|*}
 */
const BASE = setBaseUrl(`/ajax/projects/`);

/**
 * Remove a user from a project
 *
 * @param {number} projectId - identifier for a project
 * @param {number} id - identifier for a user to remove from the project
 * @returns {Promise<unknown>}
 */
export async function removeUserFromProject({ projectId, id }) {
  try {
    const { data } = await axios.delete(`${BASE}${projectId}/members?id=${id}`);
    return Promise.resolve(data);
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

/**
 * Update a members role on the project
 *
 * @param {number} projectId - identifier for a project
 * @param {number} id - identifier for a user
 * @param {string} role - new project role for the user
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function updateUserRoleOnProject({ projectId, id, role }) {
  return await axios
    .put(`${BASE}${projectId}/members/role?id=${id}&role=${role}`)
    .then(({ data }) => data);
}

/**
 * Get a list of all the available users for a project based on the query string
 *
 * @param {number} projectId - identifier for a project
 * @param query
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getAvailableUsersForProject({ projectId, query }) {
  return await axios
    .get(`${BASE}${projectId}/members/available?query=${query}`)
    .then(({ data }) => data || []);
}

/**
 * Add a new member to the project
 *
 * @param {number} projectId - identifier for a project
 * @param {number} id - identifier for the user
 * @param {string} role - project role for the user
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function addMemberToProject({ projectId, id, role }) {
  return await axios
    .post(`${BASE}${projectId}/members/add`, {
      id,
      role,
    })
    .then(({ data }) => data);
}
