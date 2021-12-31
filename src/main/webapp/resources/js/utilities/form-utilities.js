/**
 * This file is for utilities required by a form
 */

/**
 * Checks if there are 2 options which are
 * truthy such as for a checkbox
 * @param {Element} The options to check if they are truthy
 */
export function isTruthy(options) {
  if (options.length !== 2) return false;
  return (
    (typeof options[0].value === "boolean" &&
      typeof options[1].value === "boolean") ||
    options[0].value === "true" ||
    options[1].value === "true"
  );
}
