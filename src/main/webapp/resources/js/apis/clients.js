import axios from "axios";

export function getClientsPage(params = {}) {
  return axios.get("clients/ajax/list", { params });
}
