/**
 * @fileOverview This file is responsible for all asynchronous calls for
 * clients.
 */

import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl("/clients");

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
export async function addNewClient({ clientId, accessTokenValiditySeconds, authorizedGrantTypes,
                                     refresh, refreshTokenValidity, registeredRedirectUri,
                                     scope_read, scope_auto_read, scope_write, scope_auto_write }) {
  return axios
    .post(`${BASE_URL}/create`, {
      clientId,
      accessTokenValiditySeconds,
      authorizedGrantTypes,
      refresh,
      refreshTokenValidity,
      registeredRedirectUri,
      scope_read,
      scope_auto_read,
      scope_write,
      scope_auto_write
    })
    .then(({ data }) => data);
}

/**
 * Add new client.
 * @return {Promise<AxiosResponse<T>>}
 */
export async function getAddClientPage() {
  return axios.get(`${BASE_URL}/create`);
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