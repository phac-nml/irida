import {METADATA} from '../../constants';

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

MetadataAsideController.$inject = [
  '$uibModalInstance',
  'parent'
];

/**
 * Controller for the button to toggle the metadata side panel.
 */
class MetadataButtonController {
  /**
   * Constructor
   * @param {object} $rootScope angular scope handler for the root of the application
   * @param {object} $aside angular-aside object.
   * @param {object} MetadataService for fetching metadata terms.
   */
  constructor($rootScope, $aside, MetadataService) {
    this.$rootScope = $rootScope;
    this.MetadataService = MetadataService;
    this.$aside = $aside;
  }

  /**
   * Initialization function.
   */
  $onInit() {
    this.MetadataService
      .getMetadata(this.metadataUrl)
      .then(results => {
        if (Object.keys(results.metadata).length &&
          results.terms.length) {
          // By default all terms are selected.
          this.terms = results.terms.map(term => {
            return ({
              term,
              selected: true
            });
          });
          this.$rootScope.$broadcast(
            METADATA.LOADED,
            {metadata: results.metadata}
          );
        } else {
          this.$rootScope.$broadcast(METADATA.EMPTY);
        }
      }, () => {
        this.$rootScope.$broadcast(METADATA.ERROR);
      });
  }

  /**
   * EventHandler toggling metadata visibility.
   */
  handleTermVisibilityChange() {
    const columns = this.terms
      .filter(term => term.selected)
      .map(term => term.term);
    this.$rootScope.$broadcast(METADATA.UPDATED, {columns});
  }

  /**
   * EventHandler for displaying the side panel.
   */
  openMetadataAside() {
    const parent = this;

    // Open the Aside to let the user select terms.
    this.$aside
      .open({
        templateUrl: `metadataAside.tmpl.html`,
        placement: `left`,
        size: 'sm',
        openedClass: 'metadata-open',
        controllerAs: '$ctrl',
        controller: MetadataAsideController,
        resolve: {
          parent() {
            return parent;
          }
        }
      });
  }

}

MetadataButtonController.$inject = [
  '$rootScope',
  '$aside',
  'MetadataService'
];

export const MetadataButton = {
  bindings: {
    metadataUrl: '@' // Pass in the metadata url from the UI.
  },
  templateUrl: 'metadataButton.tmpl.html',
  controller: MetadataButtonController
};
