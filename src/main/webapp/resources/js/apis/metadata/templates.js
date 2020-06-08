import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`linelist/templates`);
let projectId;

/**
 * Get a list of Metadata Templates for this project.
 * @param {number} id
 * @returns {AxiosPromise}
 */
export function fetchTemplates(id) {
  projectId = id;
  return axios({
    method: "get",
    url: `${URL}?projectId=${projectId}`,
  });
}

/**
 * Save (or update) a Metadata Template
 * @param {Object} data {name, id, fields}
 * @returns {AxiosPromise}
 */
export function saveTemplate(data) {
  return axios({
    method: "post",
    url: `${URL}?projectId=${projectId}`,
    data,
  });
}
