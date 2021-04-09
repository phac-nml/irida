import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/metadata/templates`);

/**
 * Get all metadata templates associated with a project
 * @param projectId
 * @returns {Promise<AxiosResponse<any>>}
 */
export function getProjectMetadataTemplates(projectId) {
  return axios
    .get(`${BASE_URL}?projectId=${projectId}`)
    .then(({ data }) => data);
}

/**
 * Create a new metadata template within a project
 * @param {number} projectId - identifier for the project to create the template within.
 * @param {Object} parameters - details about the template (name, desc, and fields)
 * @returns {Promise<any>}
 */
export async function createProjectMetadataTemplate(projectId, parameters) {
  try {
    const { data } = await axios.post(
      `${BASE_URL}?projectId=${projectId}`,
      parameters
    );
    return data;
  } catch (e) {
    return Promise.reject(e.response.data.message);
  }
}

/**
 * Update the details in a metadata template
 * @param {Object} template - the template to update
 * @returns {Promise<*>}
 */
export async function updateMetadataTemplate(template) {
  try {
    const { data } = await axios.put(
      `${BASE_URL}/${template.identifier}`,
      template
    );
    return data.message;
  } catch (e) {
    return Promise.reject(e.response.data.message);
  }
}

/**
 * Remove a metadata template from within a project
 * @param {number} projectId - identifier for a project
 * @param {number} templateId - identifier for a metadata template
 * @returns {Promise<*>}
 */
export async function deleteMetadataTemplate(projectId, templateId) {
  try {
    const { data } = await axios.delete(
      `${BASE_URL}/${templateId}?projectId=${projectId}`
    );
    return data.message;
  } catch (e) {
    return Promise.reject(e.response.data.error);
  }
}

/**
 * Set a default metadata template for a project
 * @param templateIdId Identifier of the metadata template
 * @param projectId Identifier of the project
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function setDefaultMetadataTemplate(projectId, templateId) {
  try {
    const { data } = await axios.post(
      `${BASE_URL}/${templateId}/set-project-default?projectId=${projectId}`
    );
    return data.message;
  } catch (e) {
    return Promise.reject(e.response.data.message);
  }
}
