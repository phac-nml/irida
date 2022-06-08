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
 * @param userId - identifier for a user
 */
export async function fetchUserStatistics(userId: number): Promise<UserStatistics> {
  try {
    const { data } = await axios.get(`${URL}?userId=${userId}`);
    return data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response) {
        return Promise.reject(error.response.data.error);
      } else {
        return Promise.reject(error.message);
      }
    } else {
      return Promise.reject('An unexpected error occured');
    }
  }
}
