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

const EXPORT_BASE = setBaseUrl(`/ajax/ncbi`);

export async function getNCBIPlatforms() {
  return await fetch(`${EXPORT_BASE}/platforms`)
    .then((response) => response.json())
    .then((response) => response.platforms)
    .catch((error) => console.log(error));
}

export async function getNCBISources() {
  return await fetch(`${EXPORT_BASE}/sources`)
    .then((response) => response.json())
    .catch((error) => console.log(error));
}

export async function getNCBIStrategies() {
  return await fetch(`${EXPORT_BASE}/strategies`)
    .then((response) => response.json())
    .catch((error) => console.log(error));
}

export async function getNCBISelections() {
  return await fetch(`${EXPORT_BASE}/selections`)
    .then((response) => response.json())
    .catch((error) => console.log(error));
}
