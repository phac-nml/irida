/**
 * Class responsible for ajax call for project sample metadata fields.
 */
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`linelist/fields`);

/**
 * Get all the MetadataTemplateFields belonging to the templates withing a
 * project.These will be the table headers.
 * @param {number} projectId
 * @returns {Promise}
 */
export function fetchMetadataFields(projectId) {
  return axios({
    method: "get",
    url: `${URL}?projectId=${projectId}`
  });
}
