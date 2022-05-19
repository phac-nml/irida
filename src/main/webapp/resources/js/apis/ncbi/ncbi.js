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

const EXPORT_BASE = setBaseUrl(`/ajax/export/ncbi`);

export async function getNCBIPlatforms() {
  return await fetch(`${EXPORT_BASE}/platforms`)
    .then((response) => response.json())
    .then((response) => response.platforms)
    .catch((error) => console.log(error));
}

export async function getNCBISources() {
  return await fetch(`${EXPORT_BASE}/sources`)
    .then((response) => response.json())
    .then((response) => response)
    .catch((error) => console.log(error));
}
