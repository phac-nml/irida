import axios from "axios";

/**
 * Default function to fetch paged table data.
 * @param {string} url
 * @param {object} data = expected:
 *          { current, pageSize, sortColumn, sortDirection, search, filters }
 * @returns {Promise<AxiosResponse<T>>}
 */
export async function fetchPageTableUpdate(url, data) {
  return axios.post(url, data).then(({ data }) => data);
}
