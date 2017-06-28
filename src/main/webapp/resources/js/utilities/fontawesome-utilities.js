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
 * @return {string} icon element
 */
export function createIcon({icon = "", fixed = false}) {
  return `<i class="fa ${icon}${fixed ? ' fa-fw' : ''}"></i>`;
}
