const angular = require('angular');
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

    $scope.$on(METADATA.LOADED, (event, args) => {
      const {terms} = args;
      this.terms = terms.map(term => {
        return ({
          term,
          selected: true
        });
      });
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
  '$scope',
  '$aside',
  'MetadataService'
];

export const MetadataButton = {
  templateUrl: 'metadataButton.tmpl.html',
  controller: MetadataButtonController
};
