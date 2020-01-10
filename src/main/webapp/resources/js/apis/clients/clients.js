import axios from "axios";

export function getAllClients() {
  return axios.get("clients/ajax/list").then(({ data }) => data);
}
