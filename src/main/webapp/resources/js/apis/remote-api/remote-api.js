import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/remote_api`);

/**
 * Check the status of a specific Remote API.
 * @param {number} id - identifier for the API.
 * @returns {Promise<boolean>}
 */
export function checkConnectionStatus({ id }) {
  return axios
    .get(`${BASE_URL}/status/${id}`)
    .then(({ data }) => data)
    .catch(() => false);
}

export function getConnectionDetails({ id }) {
  return axios.get(`${BASE_URL}/${id}`).then(({ data }) => data);
}

export function deleteRemoteApi({ id }) {
  return axios.delete(`${BASE_URL}/${id}/delete`);
}
