import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import {
  projects_available_route,
  projects_members_add_route,
  projects_memberS_metadata_role_update_route,
  projects_members_remove_route,
  projects_members_role_update_route,
} from "../routes";
import { post } from "../requests";

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
  const params = new URLSearchParams([
    [projectId, String(projectId)],
    [id, String(id)],
  ]);
  try {
    const { data } = await axios.delete(
      `${projects_members_remove_route()}?${params.toString()}`
    );
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
  projectRole = "",
}) {
  const params = new URLSearchParams({
    projectRole,
    id: String(id),
    projectId: String(projectId),
  });
  try {
    return await axios
      .put(`${projects_members_role_update_route()}?${params.toString()}`)
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
    id: String(id),
    projectId: String(projectId),
    metadataRole,
  });
  try {
    return await axios
      .put(
        `${projects_memberS_metadata_role_update_route()}?${params.toString()}`
      )
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
  // TODO (Josh - 7/7/22): Why is this request in the members file?
  const params = new URLSearchParams({ projectId: String(projectId), query });
  return await axios
    .get(`${projects_available_route()}?${params.toString()}`)
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
  return await post(`${projects_members_add_route}?projectId=${projectId}`, {
    id,
    projectRole,
    metadataRole,
  });
}
