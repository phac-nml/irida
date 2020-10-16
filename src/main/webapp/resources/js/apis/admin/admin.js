import { setBaseUrl } from "../../utilities/url-utilities";
import axios from "axios";

const ADMIN_URL = setBaseUrl(`/ajax/statistics`);

/*
 * Get all the statistics for the admin panel
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getAdminStatistics() {
  return await axios.get(`${ADMIN_URL}/basic`).then(({data}) => data).catch(error => {
    throw new Error(error.response.data.error);
  });
}

/*
 * Get updated project statistics for the provided time period
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getUpdatedAdminProjectStatistics(timePeriod) {
  return await axios.get(`${ADMIN_URL}/projects`).then(({data}) => data).catch(error => {
    throw new Error(error.response.data.error);
  });
}

/*
 * Get updated analyses statistics for the provided time period
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getUpdatedAdminAnalysesStatistics(timePeriod) {
  return await axios.get(`${ADMIN_URL}/analyses`).then(({data}) => data).catch(error => {
    throw new Error(error.response.data.error);
  });
}

/*
 * Get updated sample statistics for the provided time period
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getUpdatedAdminSampleStatistics(timePeriod) {
  return await axios.get(`${ADMIN_URL}/samples`).then(({data}) => data).catch(error => {
    throw new Error(error.response.data.error);
  });
}

/*
 * Get updated user statistics for the provided time period
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getUpdatedAdminUserStatistics(timePeriod) {
  return await axios.get(`${ADMIN_URL}/users`).then(({data}) => data).catch(error => {
    throw new Error(error.response.data.error);
  });
}