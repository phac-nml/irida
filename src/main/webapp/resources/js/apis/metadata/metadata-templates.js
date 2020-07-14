import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/metadata-templates`);

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
 * Get details about a specific metadata template
 * @param {number} templateId Identifier for a metadata template
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getMetadataTemplateDetails({ templateId }) {
  return await axios.get(`${BASE_URL}/${templateId}`).then(({ data }) => data);
}

/**
 * Update either the description or name of a metadata template
 * @param {number} templateId - Identifier for a metadata template
 * @param {string} field - either 'name' or 'description'
 * @param {string} value - value to update the field to
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function updateTemplateAttribute({ templateId, field, value }) {
  return await axios
    .put(`${BASE_URL}/${templateId}?field=${field}&value=${value}`)
    .then(({ data }) => data)
    .catch((error) => error.response.data);
}

/**
 * Create a new metadata template
 * @param projectId - Identifier for the project to create the template in.
 * @param name - Name of the template
 * @param description - Template description (optional)
 * @returns {Promise<AxiosResponse<any>|void>}
 */
export async function createNewMetadataTemplate({
  projectId = window.project.id,
  name,
  description = "",
}) {
  return await axios
    .post(BASE_URL, { projectId, name, description })
    .then(({ data }) => data);
}
