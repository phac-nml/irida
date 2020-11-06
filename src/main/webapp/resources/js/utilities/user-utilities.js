import { getCookieByName } from "./cookie-utilities";

/**
 * Get the currently logged in users locale
 *
 * @returns {string|*|string}
 */
export const getUserLocale = () => getCookieByName("locale") || "en-CA";

/**
 * Get the currently logged in user username
 *
 * @returns {string|*|undefined}
 */
export const getUsername = () => getCookieByName("username");

/**
 * Get the currently logged in users identifier
 *
 * @returns {number|*|undefined}
 */
export const getUserIdentifier = () => Number(getCookieByName("user_id"));

/**
 * Get the currently logged in users system role
 *
 * @returns {string|*|undefined}
 */
export const getSystemRole = () => getCookieByName("system_role");

/**
 * Is the currently logged in user an administrator
 *
 * @returns {boolean}
 */
export const isUserAdmin = () => getSystemRole() === "ROLE_ADMIN";
