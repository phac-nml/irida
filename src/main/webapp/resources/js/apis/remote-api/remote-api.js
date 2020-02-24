import axios from "axios";

export function checkConnectionStatus({ id }) {
  return axios
    .get(`ajax/remote_api/status/${id}`)
    .then(({ data }) => data === "valid_token");
}
