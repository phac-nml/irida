/**
 * This file is for dynamically adding bootstrap features to DOM elements.
 */

/**
 * Add a bootstrap tooltip to a DOM element
 * @param {object} dom element to add tooltip to.
 * @param {string} placement defaults to top
 * @param  {string} title to add to dom
 * @return {string} formatted DOM element
 */
export function addTooltip({dom, placement = 'top', title}) {
  // Check to see the dom is a true DOM element of just a string.
  if (typeof dom === 'string') {
    const div = document.createElement('div');
    div.innerHTML = dom;
    dom = div.childNodes[0];
  }
  dom.dataset.toggle = 'tooltip';
  dom.dataset.placement = placement;
  dom.title = title || dom.title;
  return dom.outerHTML;
}
