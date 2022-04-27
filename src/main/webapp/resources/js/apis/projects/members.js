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
const BASE_URL = setBaseUrl(`/ajax/projects/members`);

/**
 * Remove a user from a project
 *
 * @param {number} projectId - identifier for a project
 * @param {number} id - identifier for a user to remove from the project
 * @returns {Promise<unknown>}
 */
export async function removeUserFromProject({ projectId, id }) {
  const params = new URLSearchParams({ projectId, id });
  try {
    const { data } = await axios.delete(`${BASE_URL}?${params.toString()}`);
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
 * @param {string} projectRole - project role for the user
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function updateUserRoleOnProject({
  projectId,
  id,
  projectRole = ""
}) {
  const params = new URLSearchParams({
    projectRole,
    id,
    projectId
  });
  try {
    return await axios
      .put(`${BASE_URL}/role?${params.toString()}`)
      .then(({ data }) => data);
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

/**
 * Update a members metadata role on the project
 *
 * @param {number} projectId - identifier for a project
 * @param {number} id - identifier for a user
 * @param {string} metadataRole - metadata role for the user
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function updateUserMetadataRoleOnProject({
  projectId,
  id,
  metadataRole = "",
}) {
  const params = new URLSearchParams({
    id,
    projectId,
    metadataRole,
  });
  try {
    return await axios
        .put(`${BASE_URL}/metadata-role?${params.toString()}`)
        .then(({ data }) => data);
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

/**
 * Get a list of all the available users for a project based on the query string
 *
 * @param {number} projectId - identifier for a project
 * @param query
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getAvailableUsersForProject({ projectId, query }) {
  const params = new URLSearchParams({ projectId, query });
  return await axios
    .get(`${BASE_URL}/available?${params.toString()}`)
    .then(({ data }) => data || []);
}

/**
 * Add a new member to the project
 *
 * @param {number} projectId - identifier for a project
 * @param {number} id - identifier for the user
 * @param {string} projectRole - project role for the user
 * @param {string} metadataRole - metadata role for the user
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function addMemberToProject({
  projectId,
  id,
  projectRole,
  metadataRole,
}) {
  return await axios
    .post(`${BASE_URL}/add?projectId=${projectId}`, {
      id,
      projectRole,
      metadataRole,
    })
    .then(({ data }) => data);
}
