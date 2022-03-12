import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/clients`);

/**
 * Revoke all tokens for a given client by identifier.
 * @param {number} id for the client
 * @return {Promise<AxiosResponse<T>>}
 */
export async function revokeClientTokens(id) {
  return await axios.delete(`${BASE_URL}/revoke?id=${id}`);
}

/**
 * Validate the identifier for client
 * @param clientId - the identifier to validate
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function validateClientId(clientId) {
  return axios.get(`${BASE_URL}/validate?clientId=${clientId}`);
}

/**
 * Create a new client with the provided details.
 * @param details - {clientId, tokenValidity, grantType, refreshToken, read, write, redirectURI}
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function createClient(details) {
  return axios.post(`${BASE_URL}`, details).then(({ data }) => data);
}

/**
 * Delete a client
 * @param {number} id - client id
 * @returns {Promise<*>}
 */
export async function deleteClient(id) {
  return axios.delete(BASE_URL, {
    params: { id },
  });
}

/**
 * Generate a new client secret
 * @param {number} id - client id
 * @returns
 */
export async function regenerateClientSecret(id) {
  return axios.put(`${BASE_URL}/secret?id=${id}`);
}

/**
 * Update the details of a client
 * @param {object} details - {clientId, tokenValidity, grantType, refreshToken, read, write, redirectURI}
 * @returns
 */
export async function updateClientDetails(details) {
  return axios.put(BASE_URL, details).then(({ data }) => data);
}
