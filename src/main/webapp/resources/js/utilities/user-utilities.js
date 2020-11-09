import { getSessionValue } from "./session-utilities";

/**
 * Get the currently logged in users locale
 *
 * @returns {string|*|string}
 */
export const getUserLocale = () => {
  return getSessionValue("locale") || "en-CA";
};

/**
 * Get the currently logged in user username
 *
 * @returns {string|*|undefined}
 */
export const getUsername = () => getSessionValue("username");

/**
 * Get the currently logged in users identifier
 *
 * @returns {number|*|undefined}
 */
export const getUserIdentifier = () => Number(getSessionValue("user_id"));

/**
 * Get the currently logged in users system role
 *
 * @returns {string|*|undefined}
 */
export const getSystemRole = () => getSessionValue("userRole");

/**
 * Is the currently logged in user an administrator
 *
 * @returns {boolean}
 */
export const isUserAdmin = () => getSystemRole() === "ROLE_ADMIN";
