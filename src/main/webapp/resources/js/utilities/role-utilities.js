/**
 * Utility method to quickly determine if the current user is an administrator
 * @returns {boolean}
 */
export function isAdmin() {
  return window?.TL?._USER?.systemRole === "ROLE_ADMIN";
}
