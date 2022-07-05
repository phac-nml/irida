/**
 * @fileOverview This file is responsible for all asynchronous calls for
 * announcements.
 */

import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import {
  announcements_create_route,
  announcements_delete_route,
  announcements_get_route,
  announcements_mark_as_read_route,
  announcements_update_route,
  announcements_user_list_route,
  announcements_user_read_route,
  announcements_user_unread_route,
} from "../routes";
import { get, post } from "../requests";

const BASE = setBaseUrl(`ajax/announcements`);

/**
 * Get an announcement.
 * @param {number} aID The announcement id.
 * @returns {Promise<AxiosResponse<T>>}
 */
export async function getAnnouncement({ aID }) {
  return await get(announcements_get_route({ aID }));
}

/**
 * Get all the read and unread announcements.
 * @returns {Promise<AxiosResponse<T>>}
 */
export async function getAnnouncements() {
  return await get(announcements_user_list_route());
}

/**
 * Get all the read announcements.
 * @returns {Promise<AxiosResponse<T>>}
 */
export async function getReadAnnouncements() {
  return await get(announcements_user_read_route());
}

/**
 * Get all the unread announcements.
 * @returns {Promise<AxiosResponse<T>>}
 */
export async function getUnreadAnnouncements() {
  return await get(announcements_user_unread_route());
}

/**
 * Mark announcement as read.
 * @returns {Promise<AxiosResponse<T>>}
 */
export async function markAnnouncementRead({ aID }) {
  return await post(announcements_mark_as_read_route({ aID }));
}

/**
 * Create a new announcement.
 * @param {string} title - the title of the new announcement.
 * @param {string} message - the content of the new announcement.
 * @param {boolean} priority - the priority of the new announcement.
 * @returns {Promise<AxiosResponse<T>>}
 */
export function createNewAnnouncement({ title, message, priority }) {
  return post(announcements_create_route(), { title, message, priority });
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
    return axios
      .put(announcements_update_route(), { id, title, message, priority })
      .then(({ data }) => data);
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
    url: announcements_delete_route(),
    data: {
      id,
    },
  }).then(({ data }) => data);
}
