import $ from "jquery";

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

export class SampleCartButton {
  constructor(node, clickHandler) {
    this.$node = $(node);
    this.$node.on("click", clickHandler);
    this.checkState();
  }

  checkState(count = 0) {
    if (count < this.$node.data("enabledAt")) {
      this.$node.prop("disabled", true);
    } else {
      this.$node.prop("disabled", false);
    }
  }
}

export class SampleExportButton {
  constructor(node, clickHandler) {
    const $node = $(node);
    this.$node = $node;
    this.$node.on("click", function() {
      clickHandler.call($node);
    });
    this.checkState();
  }

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
