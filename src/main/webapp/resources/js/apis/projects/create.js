import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * AJAX request to create a new project
 * @param {Object} details - name, description, organism, wiki, samples.
 * @returns {Promise<any>}
 */
export async function createProject(details) {
  try {
    const { data } = await axios.post(
      setBaseUrl(`/ajax/projects/new`),
      details
    );
    return data;
  } catch (e) {
    return Promise.reject(e.response.data.message);
  }
}
