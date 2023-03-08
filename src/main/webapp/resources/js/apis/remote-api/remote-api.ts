import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/remote_api`);

/**
 * Check the status of a specific Remote API.
 */
export function checkConnectionStatus({ id }: { id: number }) {
  return axios
    .get(`${BASE_URL}/status/${id}`)
    .then(({ data }) => data)
    .catch(() => false);
}

/**
 * Get the specific details about a remote api connection
 */
export function getConnectionDetails({ id }: { id: number }) {
  return axios.get(`${BASE_URL}/${id}`).then(({ data }) => data);
}

/**
 * Delete a specific remote api
 */
export function deleteRemoteApi({ id }: { id: number }) {
  return axios.delete(`${BASE_URL}/${id}/delete`);
}

/**
 * Get a list of all remote APIs
 */
export function getListOfRemoteApis() {
  return axios.get(`${BASE_URL}/apis`).then(({ data }) => data);
}

/**
 * Get a list of projects for a Remote API
 */
export function getProjectsForAPI({ id }: { id: number }) {
  return axios.get(`${BASE_URL}/${id}/projects`).then(({ data }) => data);
}

/**
 * Create a new Synchronized Project
 */
export function createSynchronizedProject({
  url,
  frequency,
}: {
  url: string;
  frequency: string;
}) {
  return axios
    .post(`${BASE_URL}/project`, { url, frequency })
    .then(({ data }) => data)
    .catch((error) => {
      return Promise.reject(error.response.data.error);
    });
}
