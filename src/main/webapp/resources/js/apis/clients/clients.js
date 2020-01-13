import axios from "axios";

/**
 * Get a list of all clients in the IRIDA instance
 * @return {Promise<*[]|any>}
 */
export async function getAllClients() {
  try {
    const { data } = await axios.get("clients/ajax/list");
    return data;
  } catch (e) {
    return [];
  }
}

/**
 * Revoke all tokens for a given client by identifier.
 * @param {number} id for the client
 * @return {Promise<AxiosResponse<T>>}
 */
export async function revokeClientTokens(id) {
  return await axios.put(`clients/ajax/revoke?id=${id}`);
}
