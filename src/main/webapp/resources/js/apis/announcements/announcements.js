/**
 * @fileOverview This file is responsible for all asynchronous calls for
 * announcements.
 */

import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE = setBaseUrl(`ajax/announcements`);

/**
 * Create a new announcement.
 * @param {string} message - the content of the new announcement.
 * @returns {Promise<AxiosResponse<T>>}
 */
export function createNewAnnouncement({ message }) {
  return axios.post(`${BASE}/create`, { message });
}

/**
 * Update an existing announcement.
 * @param {number} id - existing announcements identifier
 * @param {string} message - the updated announcement
 * @returns {Promise<AxiosResponse<T>>}
 */
export function updateAnnouncement({ id, message }) {
  return axios.put(`${BASE}/update`, { id, message });
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
