import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`/ajax/user/statistics`);

/**
 * Get user statistics
 * @param {number} userId - identifier for a user
 * @returns {Promise<any>}
 */
export async function fetchUserStatistics(userId) {
  try {
    const { data } = await axios.get(`${URL}?userId=${userId}`);
    return data;
  } catch (e) {
    return Promise.reject(e.response.data.error);
  }
}
