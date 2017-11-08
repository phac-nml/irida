function isButton(element) {
  return element.prop("nodeName") === "BUTTON";
}

function disableElement(element) {
  if (isButton(element)) {
    element.prop("disabled", true);
  } else {
    element.addClass("disabled");
  }
}

function enableElement(element) {
  if (isButton(element)) {
    element.prop("disabled", false);
  } else {
    element.removeClass("disabled");
  }
}

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
    /*
    If the node is a button then it does not need to reference the parent element
    Everything should be on the button itself.
     */
    this.$node = node.nodeName === "BUTTON" ? $(node) : $(node.parentElement);
    this.enabledAt = this.$node.data("enabledAt");
    this.enabledMsg = this.$node.data("enabledMsg");
    this.allowAssociated = this.$node.data("allowAssociated");
    this.associatedMsg = this.$node.data("associatedMsg");

    if (typeof listenerFn === "function") {
      node.addEventListener("click", listenerFn, false);
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
    this.$node.tooltip("destroy");

    if (hasAssociated && !this.allowAssociated) {
      disableElement(this.$node, this.isButton);
      // Activate the tooltip
      this.$node.tooltip({
        container: "body",
        placement: "right",
        title: this.associatedMsg
      });
    } else {
      if (count < this.enabledAt) {
        disableElement(this.$node, this.isButton);
        // Activate the tooltip
        this.$node.tooltip({
          container: "body",
          placement: "right",
          title: this.enabledMsg
        });
      } else {
        enableElement(this.$node, this.isButton);
      }
    }
  }
}
