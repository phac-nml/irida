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
export async function addNewClient({ clientId, accessTokenValiditySeconds, authorizedGrantTypes }) {
  return axios
    .post(`${BASE_URL}/create`, {
      clientId,
      accessTokenValiditySeconds,
      authorizedGrantTypes,

    })
    .then(({ data }) => data);
}