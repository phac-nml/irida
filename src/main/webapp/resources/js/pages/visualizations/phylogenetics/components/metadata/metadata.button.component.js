import {METADATA} from '../../constants';

class MetadataAsideController {
  constructor($uibModalInstance, parent) {
    this.terms = parent.terms;
    this.parent = parent;
    this.modal = $uibModalInstance;
  }

  termSelectionChange() {
    this.parent.handleTermVisibilityChange();
  }

  close() {
    this.modal.dismiss();
  }
}

MetadataAsideController.$inject = [
  '$uibModalInstance',
  'parent'
];

class MetadataButtonController {
  constructor($rootScope, $aside, MetadataService) {
    this.$rootScope = $rootScope;
    this.MetadataService = MetadataService;
    this.$aside = $aside;
  }

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

  handleTermVisibilityChange() {
    const columns = this.terms
      .filter(term => term.selected)
      .map(term => term.term);
    this.$rootScope.$broadcast(METADATA.UPDATED, {columns});
  }

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
    metadataUrl: '@'
  },
  templateUrl: 'metadataButton.tmpl.html',
  controller: MetadataButtonController
};
