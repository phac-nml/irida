/**
 * Responsible for all AJAX calls related to NCBI exports.
 */

import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * Get a list of ncbi exports for the current project.
 * @returns {Promise<any>}
 */
export async function getProjectNCBIExports() {
  const response = await fetch(
    setBaseUrl(`/ajax/ncbi/project/${window.project.id}/list`)
  );
  return response.json();
}
