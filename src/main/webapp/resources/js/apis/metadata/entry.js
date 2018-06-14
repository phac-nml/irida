/**
 * Class responsible for ajax call for project sample metadata entries.
 */
import axios from "axios";
import qs from "qs";

const BASE_URL = `${window.TL.BASE_URL}linelist/entries`;

/**
 * Get all metadata belonging to samples in the current project.
 * These will be the table content
 * @param {number} projectId
 * @returns {Promise}
 */
export function fetchMetadataEntries(projectId) {
  return axios({
    method: "get",
    url: `${BASE_URL}?projectId=${projectId}`
  });
}

export function saveMetadataEntryField(sampleId, value, label) {
  const params = new URLSearchParams();
  params.append("sampleId", sampleId);
  params.append("value", value);
  params.append("label", label);
  return axios.post(BASE_URL, params);
}
