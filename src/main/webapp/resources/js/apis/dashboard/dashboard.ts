import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`/ajax/user/statistics`);

export interface UserStatistics {
  numberOfProjects: number;
  numberOfSamples: number;
  numberOfAnalyses: number;
}

/**
 * Get user statistics
 * @param {number} userId - identifier for a user
 * @returns {Promise<any>}
 */
export async function fetchUserStatistics(userId: number): Promise<UserStatistics | never> {
  try {
    const { data } = await axios.get(`${URL}?userId=${userId}`);
    return data;
  } catch (e: any) {
    return Promise.reject(e.response.data.error);
  }
}
