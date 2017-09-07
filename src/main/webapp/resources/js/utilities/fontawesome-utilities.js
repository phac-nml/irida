/**
 * This file is for create dynamic font-awesome icons.
 */

export const ICONS = {
  download: "fa-download", // Downloading,
  trash: "fa-trash", // Deleting
  remove: "fa-times", // Removing
  edit: "fa-pencil", // Editing
  checkmark: "fa-check"
};

/**
 * Create a font-awesome icon within an `i` DOM element
 * @param {string} icon to display
 * @param {boolean} fixed width
 * @return {Element} icon element
 */
export function createIcon({ icon = "", fixed = false }) {
  const i = `<i class="fa ${icon}${fixed ? " fa-fw" : ""}"></i>`;
  const div = document.createElement("div");
  div.innerHTML = i;
  return div.childNodes[0];
}
