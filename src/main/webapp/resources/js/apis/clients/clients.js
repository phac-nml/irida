import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import {
  clients_create_route,
  clients_delete_route,
  clients_regenerate_secret_route,
  clients_revoke_token_route,
  clients_update_route,
  clients_validate_route,
} from "../routes";

const BASE_URL = setBaseUrl(`/ajax/clients`);

/**
 * Revoke all tokens for a given client by identifier.
 * @param {number} id for the client
 * @return {Promise<AxiosResponse<T>>}
 */
export async function revokeClientTokens(id) {
  return await axios.delete(`${clients_revoke_token_route()}?id=${id}`);
}

/**
 * Validate the identifier for client
 * @param clientId - the identifier to validate
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function validateClientId(clientId) {
  return axios.get(`${clients_validate_route()}?clientId=${clientId}`);
}

/**
 * Create a new client with the provided details.
 * @param details - {clientId, tokenValidity, grantType, refreshToken, read, write, redirectURI}
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function createClient(details) {
  return axios.post(clients_create_route(), details).then(({ data }) => data);
}

/**
 * Delete a client
 * @param {number} id - client id
 * @returns {Promise<*>}
 */
export async function deleteClient(id) {
  return axios.delete(clients_delete_route(), {
    params: { id },
  });
}

/**
 * Generate a new client secret
 * @param {number} id - client id
 * @returns
 */
export async function regenerateClientSecret(id) {
  return axios.put(`${clients_regenerate_secret_route()}?id=${id}`);
}

/**
 * Update the details of a client
 * @param {object} details - {clientId, tokenValidity, grantType, refreshToken, read, write, redirectURI}
 * @returns
 */
export async function updateClientDetails(details) {
  return axios.put(clients_update_route(), details).then(({ data }) => data);
}
