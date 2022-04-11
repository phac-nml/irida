import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * @file API methods for the currently logged in user
 */

/**
 * Get details about the currently logged in user
 * @param {number | undefined} projectId - identifier for a project
 * @returns {Promise<unknown>}
 */
export async function fetchCurrentUserDetails({ projectId }) {
  try {
    const params = new URLSearchParams();
    if (projectId) {
      params.append("projectId", `${projectId}`);
    }
    const { data } = await axios.get(
      setBaseUrl(`/ajax/users/current?${params.toString()}`)
    );
    return Promise.resolve(data);
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}
