import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * Check the status of a specific Remote API.
 * @param {number} id - identifier for the API.
 * @returns {Promise<boolean>}
 */
export function checkConnectionStatus({ id }) {
  return axios
    .get(setBaseUrl(`ajax/remote_api/status/${id}`))
    .then(({ data }) => data === "valid_token");
}
