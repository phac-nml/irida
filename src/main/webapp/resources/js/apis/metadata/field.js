/**
 * Class responsible for ajax call for project sample metadata fields.
 */
import axios from "axios";

/**
 * Get all the MetadataTemplateFields belonging to the templates withing a
 * project.These will be the table headers.
 * @param {number} projectId
 * @returns {Promise}
 */
export function fetchMetadataFields(projectId) {
  return axios({
    method: "get",
    url: `linelist/fields?projectId=${projectId}`
  });
}
