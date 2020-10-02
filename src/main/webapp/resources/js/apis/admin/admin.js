import { setBaseUrl } from "../../utilities/url-utilities";
import axios from "axios";

const ADMIN_URL = setBaseUrl(`/ajax/admin`);

/*
 * Get all the statistics for the admin panel
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getAdminStatistics(timePeriod) {
  try {
    const { data } = await axios.get(`${ADMIN_URL}/statistics`, {
      params: {
        timePeriod
      }
    });
    return data;
  }  catch (error) {
    return { error };
  }
}

/*
 * Get updated project statistics for the provided time period
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getUpdatedAdminProjectStatistics(timePeriod) {
  try {
    const { data } = await axios.get(`${ADMIN_URL}/project-statistics/`);
    return data;
  }  catch (error) {
    return { error };
  }
}

/*
 * Get updated analyses statistics for the provided time period
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getUpdatedAdminAnalysesStatistics(timePeriod) {
  try {
    const { data } = await axios.get(`${ADMIN_URL}/analyses-statistics/`);
    return data;
  }  catch (error) {
    return { error };
  }
}

/*
 * Get updated sample statistics for the provided time period
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getUpdatedAdminSampleStatistics(timePeriod) {
  try {
    const { data } = await axios.get(`${ADMIN_URL}/sample-statistics/`);
    return data;
  }  catch (error) {
    return { error };
  }
}

/*
 * Get updated user statistics for the provided time period
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getUpdatedAdminUserStatistics(timePeriod) {
  try {
    const { data } = await axios.get(`${ADMIN_URL}/user-statistics/`);
    return data;
  }  catch (error) {
    return { error };
  }
}