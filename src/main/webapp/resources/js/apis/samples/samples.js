import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`ajax/samples`);

/**
 * Get details about a particular sample
 * NOTE: Does not include file information.
 * @param {number} id - identifier for a sample
 * @returns {Promise<any>}
 */
export const fetchSampleDetails = async (id) => {
  try {
    const { data } = await axios.get(`${URL}/${id}/details`);
    return data;
  } catch (e) {
    return Promise.reject(e.response.data.error);
  }
};

/**
 * Get file details for a sample
 * @param {number} sampleId - identifier for a sample
 * @param {number} projectId - identifier for a project (if the sample is in the cart), not required.
 * @returns {Promise<any>}
 */
export async function fetchSampleFiles({ sampleId, projectId }) {
  try {
    const response = await axios(
      `${URL}/${sampleId}/files${projectId ? `?projectId=${projectId}` : ""}`
    );
    return response.data;
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}
