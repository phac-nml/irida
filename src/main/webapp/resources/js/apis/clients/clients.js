/**
 * @fileOverview This file is responsible for all asynchronous calls for
 * clients.
 */

import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * Revoke all tokens for a given client by identifier.
 * @param {number} id for the client
 * @return {Promise<AxiosResponse<T>>}
 */
export async function revokeClientTokens(id) {
  return await axios.delete(`clients/ajax/revoke?id=${id}`);
}

/**
 * Add new client.
 * @return {Promise<AxiosResponse<T>>}
 */
export async function addNewClient() {
  return await axios.post(`clients/create`);
}