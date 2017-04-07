const angular = require('angular');
import {METADATA} from '../../constants';

class MetadataButtonController {
  constructor($rootScope, $scope, $aside, MetadataService) {
    this.$rootScope = $rootScope;
    this.MetadataService = MetadataService;
    this.$aside = $aside;
    this.terms = [];

    // Register listeners
    $scope.$on(METADATA.TEMPLATE, (e, args) => {
      const {fields} = args;
      const existing = angular.copy(this.terms);
      const newOrder = [];

      if (fields) { // Add the visible fields in order
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
      const showFields = typeof fields === 'undefined';
      existing.forEach(term => {
        term.selected = showFields;
        newOrder.push(term);
      });

      // Update the UI;
      this.terms = newOrder;
      this.handleTermVisibilityChange();
    });
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
  '$scope',
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
