/**
 * @fileOverview This file is responsible for all asynchronous calls for
 * announcements.
 */

import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE = setBaseUrl(`ajax/announcements`);

/**
 * Get all the read announcements.
 * @returns {Promise<AxiosResponse<T>>}
 */
export function getReadAnnouncements() {
  try {
    return axios.get(`${BASE}/user/read`);
  } catch (error) {
    return Promise.reject(error.response.data.error);
  }
}

/**
 * Get all the unread announcements.
 * @returns {Promise<AxiosResponse<T>>}
 */
export function getUnreadAnnouncements() {
  try {
    return axios.get(`${BASE}/user/unread`);
  } catch (error) {
    return Promise.reject(error.response.data.error);
  }
}

/**
 * Mark announcement as read.
 * @returns {Promise<AxiosResponse<T>>}
 */
export function markAnnouncementRead({ aID }) {
  try {
    return axios.post(`${BASE}/read/${aID}`);
  } catch (error) {
    return Promise.reject(error.response.data.error);
  }
}

/**
 * Create a new announcement.
 * @param {string} title - the title of the new announcement.
 * @param {string} message - the content of the new announcement.
 * @param {boolean} priority - the priority of the new announcement.
 * @returns {Promise<AxiosResponse<T>>}
 */
export function createNewAnnouncement({ title, message, priority }) {
  try {
    return axios.post(`${BASE}/create`, { title, message, priority });
  } catch (error) {
    return Promise.reject(error.response.data.error);
  }
}

/**
 * Update an existing announcement.
 * @param {number} id - existing announcements identifier
 * @param {string} title - the new title of the updated announcement
 * @param {string} message - the new message of the updated announcement
 * @param {boolean} priority - the new priority of the updated announcement
 * @returns {Promise<AxiosResponse<T>>}
 */
export function updateAnnouncement({ id, title, message, priority }) {
  try {
    return axios.put(`${BASE}/update`, { id, title, message, priority });
  } catch (error) {
    return Promise.reject(error.response.data.error);
  }
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
      id,
    },
  });
}
