import angular from "angular";
import { METADATA } from "../../constants";

/**
 * Controller for the side panel that displays
 * all the possible metadata terms to toggle.
 */
class MetadataAsideController {
  /**
   * Constructor
   * @param {object} $uibModalInstance  angular-ui modal instance.
   * @param {object} parent parent controller.
   */
  constructor($uibModalInstance, parent) {
    this.terms = parent.terms;
    this.parent = parent;
    this.modal = $uibModalInstance;
  }

  /**
   * EventListener for changes to the visibility of a metadata term.
   */
  termSelectionChange() {
    this.parent.handleTermVisibilityChange();
  }

  /**
   * EventHandler for closing the sidebar.
   */
  close() {
    this.modal.dismiss();
  }
}

MetadataAsideController.$inject = ["$uibModalInstance", "parent"];

/**
 * Controller for the button to toggle the metadata side panel.
 */
class MetadataButtonController {
  /**
   * Constructor
   * @param {object} $rootScope angular scope handler for the root of the application
   * @param {object} $scope angular scope for current dom
   * @param {object} $aside angular-aside object.
   */
  constructor($rootScope, $scope, $aside) {
    this.$rootScope = $rootScope;
    this.$aside = $aside;
    this.terms = [];

    // Register listeners
    $scope.$on(METADATA.TEMPLATE, (e, args) => {
      const { fields } = args;
      const existing = angular.copy(this.terms);
      const newOrder = [];

      if (fields) {
        // Add the visible fields in order
        fields.forEach(field => {
          const index = existing.findIndex(t => {
            return t.term === field;
          });
          if (index >= 0) {
            const item = existing.splice(index, 1)[0];
            item.selected = true;
            newOrder.push(item);
          }
        });
      }

      // Hide remainder of fields;
      // typeof fields === 'undefined' would evaluate to try only for the show all fields option.
      const showFields = typeof fields === "undefined";
      existing.forEach(term => {
        term.selected = showFields;
        newOrder.push(term);
      });

      // Update the UI;
      this.terms = newOrder;
      this.handleTermVisibilityChange();
    });

    $scope.$on(METADATA.LOADED, (event, args) => {
      const { terms } = args;
      this.terms = terms.map(term => {
        return {
          term,
          selected: true
        };
      });
    });
  }

  /**
   * EventHandler toggling metadata visibility.
   */
  handleTermVisibilityChange() {
    const columns = this.terms
      .filter(term => term.selected)
      .map(term => term.term);
    this.$rootScope.$broadcast(METADATA.UPDATED, { columns });
  }

  /**
   * EventHandler for displaying the side panel.
   */
  openMetadataAside() {
    const parent = this;

    // Open the Aside to let the user select terms.
    this.$aside.open({
      templateUrl: `metadataAside.tmpl.html`,
      placement: `left`,
      size: "sm",
      openedClass: "metadata-open",
      controllerAs: "$ctrl",
      controller: MetadataAsideController,
      resolve: {
        parent() {
          return parent;
        }
      }
    });
  }
}

MetadataButtonController.$inject = ["$rootScope", "$scope", "$aside"];

export const MetadataButton = {
  bindings: {
    metadataUrl: "@" // Pass in the metadata url from the UI.
  },
  templateUrl: "metadataButton.tmpl.html",
  controller: MetadataButtonController
};
