/**
 * Get a specific cookie from document.cookie.
 * If the cookie does not exist return undefiend.
 * @param name
 * @returns {string|*|undefined}
 */
export const getCookieByName = (name = "") => {
  if (!name) return undefined;
  const cookie = document.cookie
    .split(";")
    .find((row) => row.trim().startsWith(name));
  return cookie ? cookie.split("=")[1] : undefined;
};
