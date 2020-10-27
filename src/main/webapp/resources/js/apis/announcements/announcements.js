/**
 * @fileOverview This file is responsible for all asynchronous calls for
 * announcements.
 */

import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE = setBaseUrl(`ajax/announcements`);

/**
 * Create a new announcement.
 * @param {string} title - the title of the new announcement.
 * @param {string} message - the content of the new announcement.
 * @param {boolean} priority - the priority of the new announcement.
 * @returns {Promise<AxiosResponse<T>>}
 */
export function createNewAnnouncement({ title, message, priority }) {
  return axios.post(`${BASE}/create`, { title, message, priority });
}

/**
 * Update an existing announcement.
 * @param {number} id - existing announcements identifier
 * @param {string} title - the updated title of the new announcement.
 * @param {string} message - the updated content of the new announcement.
 * @param {boolean} priority - the updated priority of the new announcement.
 * @returns {Promise<AxiosResponse<T>>}
 */
export function updateAnnouncement({ id, title, message, priority }) {
  return axios.put(`${BASE}/update`, { id, title, message, priority });
}

/**
 * Delete an announcement.
 * @param {number} id - the identifier for the announcement to delete.
 * @returns {AxiosPromise}
 */
export function deleteAnnouncement({ id }) {
  return axios({
    method: "delete",
    url: `${BASE}/delete`,
    data: {
      id
    }
  });
}
