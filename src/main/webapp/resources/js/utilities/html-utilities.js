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
 * @param {string} htmlString String to escape
 * @returns {string} Escaped string
 */
export function escapeHtml(htmlString) {
  return String(htmlString).replace(/[&<>"'`=/]/g, s => CHAR_TO_ESCAPED[s]);
}

/**
 * Create a new HTML element node given some HTML string
 *
 * @param {string} htmlString HTML string
 * @return {Node} HTML node element
 */
export function newElement(htmlString) {
  const frag = document.createRange().createContextualFragment(htmlString);
  return frag.firstChild;
}
