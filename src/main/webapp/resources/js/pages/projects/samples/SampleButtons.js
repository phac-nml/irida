/**
 * This file is used to handle the buttons in the project samples page toolbar.
 * Each button type is responsible for setting its enabled state and displaying
 * tooltips explaining to the user what the state means.
 */

import $ from "jquery";

/**
 * Check the state of the calling button to see if it should be enabled or not.
 * @param count the number if samples currently selected.
 * @param hasAssociated if there are associated project currently displayed.
 */
function checkState(count, hasAssociated) {
  // Remove the tooltip. A new one will be created based on the
  // information provided.
  this.$node.tooltip("destroy");
  if (hasAssociated) {
    this.$node.parent().addClass("disabled");
    // Activate the tooltip
    this.$node.tooltip({
      container: "body",
      placement: "right",
      title: this.$node.data("associatedMsg")
    });
  } else {
    if (count < this.$node.data("enabledAt")) {
      this.$node.parent().addClass("disabled");
      // Activate the tooltip
      this.$node.tooltip({
        container: "body",
        placement: "right",
        title: this.$node.data("enabledMsg")
      });
    } else {
      this.$node.parent().removeClass("disabled");
    }
  }
}

/**
 * Class representing the add sample/s to cart button.
 */
export class SampleCartButton {
  /**
   * Create the cart button
   * @param {object} node actual DOM node for the button.
   * @param clickHandler button click handler
   */
  constructor(node, clickHandler) {
    this.$node = $(node);
    this.$node.on("click", clickHandler);
    this.checkState();
  }

  /**
   * Check the state the button should be in.
   * @param  {number} count current number of selected samples (defaults to 0)
   */
  checkState(count = 0) {
    if (count < this.$node.data("enabledAt")) {
      this.$node.prop("disabled", true);
    } else {
      this.$node.prop("disabled", false);
    }
  }
}


/**
 * Class representing sample export buttons
 */
export class SampleExportButton {
  /**
   * Create a sample export button
   * @param {object} node actual DOM node for the button.
   * @param clickHandler button click handler
   */
  constructor(node, clickHandler) {
    const $node = $(node);
    this.$node = $node;
    this.$node.on("click", function() {
      if (!$(this).parent().hasClass("disabled")) {
        clickHandler.call($node);
      }
    });
    this.checkState();
  }

  /**
   * Check the state the button should be in.
   * @param  {number} count current number of selected samples (defaults to 0)
   * @param {boolean} hasAssociated if associated projects are displayed in the table.
   */
  checkState(count = 0, hasAssociated = false) {
    checkState.call(this, count, hasAssociated);
  }
}

/**
 * This class represents the state and function of buttons within the
 * project > sample > Sample Tools dropdown menu.
 */
export class SampleDropdownButton {
  /**
   * Link the dom and the EventListener for a button.
   * @param {node} node - actual button DOM node.
   */
  constructor(node) {
    const btn = this;
    this.$node = $(node);

    this.$node.on("click", function() {
      btn.clickHandler();
    });
    this.checkState();
  }

  clickHandler() {
    if (!this.$node.parent().hasClass("disabled")) {
      $("#js-modal-wrapper").modal("show", this.$node);
    }
  }

  /**
   * Check to see if the button should be disabled based upon the
   * number of currently selected samples
   * @param {number} count of selected samples
   * @param {boolean} hasAssociated whether associated projects are being displayed.
   */
  checkState(count = 0, hasAssociated = false) {
    checkState.call(this, count, hasAssociated);
  }
}
