/**
 * @file AJAX request for metadata
 */
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`/ajax/metadata`);

/**
 * Geta  list of all metadata roles.
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getMetadataRoles() {
  return await axios.get(`${URL}/roles`).then(({ data }) => data);
}
