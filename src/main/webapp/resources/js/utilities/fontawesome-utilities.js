/**
 * This file is for create dynamic font-awesome icons.
 */

export const ICONS = {
  download: "fa-download",  // Downloading,
  trash: "fa-trash"         // Deleting
};

/**
 * Create a font-awesome icon within an `i` DOM element
 * @param {string} icon to display
 * @param {boolean} fixed width
 * @return {Element} icon element
 */
export function createIcon({icon = "", fixed = false}) {
  const i = document.createElement("i");
  i.classList.add("fa", icon);
  if (fixed) {
    i.classList.add("fa-fw");
  }
  return i;
}
