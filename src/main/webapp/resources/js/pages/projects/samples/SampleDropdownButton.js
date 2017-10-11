export default class SampleDropdownButton {
  constructor(node, listenerFn) {
    this.button = node;
    this.enabledAt = this.button.dataset.enabledAt;

    if (typeof listenerFn === "function") {
      this.button.addEventListener("click", listenerFn, false);
    }

    this.checkState(0);
  }

  /**
   * Check to see if the button should be disabled based upon the
   * number of currently selected samples
   * @param {number} count of selected samples
   */
  checkState(count) {
    this.button.disabled = count < this.enabledAt;
  }
}
