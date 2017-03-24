const angular = require('angular');

class AddMetadataFieldController {
  constructor($uibModalInstance, MetadataFieldService) {
    this.service = MetadataFieldService;
    this.modal = $uibModalInstance;
    this.field = {};
    this.list = [];
  }

  queryName(query) {
    if (query.length > 2) {
      this
        .service
        .query({query})
        .$promise
        .then(results => {
          if (results.length > 0) {
            this.list = results;
          } else {
            this.list = [{
              id: undefined,
              label: query,
              type: 'text'
            }];
          }
        });
    } else {
      this.list = [];
    }
  }

  addField() {
    this.modal.close(angular.copy(this.field));
  }
}

AddMetadataFieldController.$inject = [
  '$uibModalInstance',
  'MetadataFieldService'
];

export function addMetadataField($uibModal) {
  return function() {
    const options = {
      templateUrl: `addMetadataField.tmpl.html`,
      controllerAs: 'model',
      controller: AddMetadataFieldController
    };

    const modal = $uibModal.open(options);

    return modal.result;
  };
}
