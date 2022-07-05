import { get } from "../requests";
import { dashboard_route } from "../routes";

export interface UserStatistics {
  numberOfProjects: number;
  numberOfSamples: number;
  numberOfAnalyses: number;
}

/**
 * Get user statistics
 * @param userId - identifier for a user
 */
export async function fetchUserStatistics(
  userId: number
): Promise<UserStatistics> {
  return await get(`${dashboard_route()}?userId=${userId}`);
}
