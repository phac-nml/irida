/**
 * @file AJAX request for metadata
 */
import { get } from "../requests";
import { metadata_roles_route } from "../routes";

/**
 * Geta  list of all metadata roles.
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getMetadataRoles() {
  return get(metadata_roles_route());
}
