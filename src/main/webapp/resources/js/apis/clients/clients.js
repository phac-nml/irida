import axios from "axios";

export function getAllClients() {
  return axios.get("clients/ajax/list").then(({ data }) => data);
}

export function revokeClientTokens(id) {
  return axios.put(`clients/ajax/revoke?id=${id}`);
}
