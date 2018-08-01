/**
 * Functions for working with HTML.
 */

const CHAR_TO_ESCAPED = {
  "&": "&amp;",
  "<": "&lt;",
  ">": "&gt;",
  '"': "&quot;",
  "'": "&#39;",
  "/": "&#x2F;",
  "`": "&#x60;",
  "=": "&#x3D;"
};

/**
 * Replace special characters in a string with escaped characters
 * @param string String to escape
 * @returns {string} Escaped string
 */
export function escapeHtml(string) {
  return String(string).replace(/[&<>"'`=\/]/g, s => CHAR_TO_ESCAPED[s]);
}
