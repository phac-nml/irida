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

export async function getMetadataTemplateDetails({ templateId }) {
  return await axios
    .get(setBaseUrl(`${BASE_URL}/${templateId}`))
    .then(({ data }) => data);
}

export async function updateTemplateAttribute({ templateId, field, value }) {
  return await axios
    .put(`${BASE_URL}/${templateId}?field=${field}&value=${value}`)
    .then(({ data }) => data)
    .catch((error) => console.log(error.response));
}
