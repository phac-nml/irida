import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/metadata-templates`);

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
