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
  return String(htmlString).replace(/[&<>"'`=\/]/g, s => CHAR_TO_ESCAPED[s]);
}

/**
 * Create a new HTML Element given some HTML string
 *
 * @param {string} htmlString HTML string
 * @return {Element} HTML element
 */
export function newElement(htmlString) {
  const $tmpDiv = document.createElement("div");
  $tmpDiv.innerHTML = htmlString;
  return $tmpDiv.children[0];
}
