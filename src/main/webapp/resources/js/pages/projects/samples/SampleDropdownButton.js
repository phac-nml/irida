/**
 * This class represents the state and function of buttons within the
 * project > sample > Sample Tools dropdown menu.
 */
export default class SampleDropdownButton {
  /**
   * Link the dom and the EventListener for a button.
   * @param {node} node - actual button DOM node.
   * @param {function} listenerFn - EventHandler for the click event.
   */
  constructor(node, listenerFn) {
    this.button = node;
    this.enabledAt = this.button.dataset.enabledAt;
    this.allowAssociated = this.button.allowAssociated;

    if (typeof listenerFn === "function") {
      this.button.addEventListener("click", listenerFn, false);
    }

    this.checkState(0, false);
  }

  /**
   * Check to see if the button should be disabled based upon the
   * number of currently selected samples
   * @param {number} count of selected samples
   * @param {boolean} hasAssociated whether associated projects are being displayed.
   */
  checkState(count, hasAssociated) {
    if (hasAssociated && !this.allowAssociated) {
      this.button.disabled = true;
    } else {
      this.button.disabled = count < this.enabledAt;
    }
  }
}
