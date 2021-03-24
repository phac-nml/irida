/**
 * Class responsible for ajax call for project sample metadata fields.
 */
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const OLD_URL = setBaseUrl(`linelist/fields`);

/**
 * @deprecated
 * Get all the MetadataTemplateFields belonging to the templates withing a
 * project.These will be the table headers.
 * @param {number} projectId
 * @returns {Promise}
 */
export function fetchMetadataFields(projectId) {
  return axios({
    method: "get",
    url: `${OLD_URL}?projectId=${projectId}`,
  });
}

const URL = setBaseUrl(`/ajax/metadata/fields`);

/**
 * Get all metadata fields associated with samples in a given project.
 * @param {number} projectId - identifier for a project
 * @returns {Promise<any>}
 */
export async function getMetadataFieldsForProject(projectId) {
  try {
    const { data } = await axios.get(`${URL}?projectId=${projectId}`);
    return data;
  } catch (e) {
    return e.response.data.message;
  }
}
