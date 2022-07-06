/**
 * Class responsible for ajax call for project sample metadata entries.
 */
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import { metadata_entries_route } from "../routes";

const URL = setBaseUrl(`linelist/entries`);

/**
 * Get all metadata belonging to samples in the current project.
 * These will be the table content
 * @param {number} projectId
 * @returns {Promise}
 */
export function fetchMetadataEntries({ projectId, current, pageSize }) {
  const params = new URLSearchParams([
    ["projectId", String(projectId)],
    ["current", String(current)],
    ["pageSize", String(pageSize)],
  ]);
  return axios({
    method: "get",
    url: `${metadata_entries_route()}?${params.toString()}`,
  }).then((response) => response.data.content);
}

/**
 * Save a metadata term back to a sample
 * @param {number} sampleId - identifier for a sample
 * @param {string} value - new metadata value
 * @param {string} label - metadata term to update
 * @returns {*}
 */
export function saveMetadataEntryField(sampleId, value, label) {
  const params = new URLSearchParams();
  params.append("sampleId", String(sampleId));
  params.append("value", value);
  params.append("label", label);
  return axios.post(metadata_entries_route(), params);
}
