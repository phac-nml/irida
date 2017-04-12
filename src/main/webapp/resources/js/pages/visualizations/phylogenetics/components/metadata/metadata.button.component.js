import {METADATA} from '../../constants';

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

  handleTermVisibilityChange(term) {
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
        controllerAs: '$ctrl',
        controller(terms) {
          this.termSelectionChange = term => {
            parent.handleTermVisibilityChange(term);
          };
          this.terms = terms;
        },
        resolve: {
          terms() {
            return parent.terms;
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
