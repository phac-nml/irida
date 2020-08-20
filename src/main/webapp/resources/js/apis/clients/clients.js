/**
 * @fileOverview This file is responsible for all asynchronous calls for
 * clients.
 */

import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl("/ajax/clients");

/**
 * Revoke all tokens for a given client by identifier.
 * @param {number} id for the client
 * @return {Promise<AxiosResponse<T>>}
 */
export async function revokeClientTokens(id) {
  return await axios.delete(`${BASE_URL}/ajax/revoke?id=${id}`);
}

/**
 * Add new client.
 * @return {Promise<AxiosResponse<T>>}
 */
export async function addNewClient({
  clientId,
  scopeWrite,
  scopeRead,
  tokenValidity,
  grantType,
  refreshTokenValidity,
  redirectURI = "",
}) {
  return axios
    .post(`${BASE_URL}/create`, {
      clientId,
      scopeWrite,
      scopeRead,
      tokenValidity,
      grantType,
      refreshTokenValidity,
      redirectURI,
    })
    .then(({ data }) => data);
}

/**
 * Get details about a specific client
 *
 * @param {number} id for a client
 * @returns {Promise<AxiosResponse<any>>}
 */
export function getClientDetails(id) {
  return axios.get(`${BASE_URL}/${id}`).then(({ data }) => data);
}

/**
 * Remove Client with a given ID
 *
 * @param {number} id for a client
 * @returns {Promise<AxiosResponse<any>>}
 */
export function removeClient(id) {
  return axios.post(`${BASE_URL}/remove`, { id }).then(({ data }) => data);
}

/**
 * Update the Client fields
 * @param {number} id for the client
 * @param {string} field to update
 * @param {string} value to set
 * @returns {Promise<AxiosResponse<any>>}
 */
export function updateClientDetails({ id, field, value }) {
  return axios.put(`${BASE_URL}/${id}/update `, {
    field,
    value,
  });
}
