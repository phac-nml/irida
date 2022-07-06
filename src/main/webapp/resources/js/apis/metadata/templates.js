import axios from "axios";
import { metadata_templates_project_route } from "../routes";

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
    url: `${metadata_templates_project_route()}?projectId=${projectId}`,
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
    url: `${metadata_templates_project_route()}?projectId=${projectId}`,
    data,
  });
}
