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
    this.$parent = $(this.button.parentElement);
    this.enabledAt = this.$parent.data("enabledAt");
    this.enabledMsg = this.$parent.data("enabledMsg");
    this.allowAssociated = this.$parent.data("allowAssociated");
    this.associatedMsg = this.$parent.data("associatedMsg");

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
    // Remove the tooltip. A new one will be created based on the
    // information provided.
    this.$parent.tooltip("destroy");

    if (hasAssociated && !this.allowAssociated) {
      this.$parent.addClass("disabled");

      // Activate the tooltip
      this.$parent.tooltip({
        container: "body",
        placement: "right",
        title: this.associatedMsg
      });
    } else {
      if (count < this.enabledAt) {
        this.$parent.addClass("disabled");

        // Activate the tooltip
        this.$parent.tooltip({
          container: "body",
          placement: "right",
          title: this.enabledMsg
        });
      } else {
        this.$parent.removeClass("disabled");
      }
    }
  }
}
