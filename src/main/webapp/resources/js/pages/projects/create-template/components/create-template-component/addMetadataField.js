const angular = require('angular');

function filterFields(tableFields, serverResults) {
  return serverResults
    .filter(field => {
      return !tableFields
        .find(tf => {
          return tf.id === field.id;
        });
    });
}

class AddMetadataFieldController {
  constructor($scope, $uibModalInstance, MetadataFieldService, fields) {
    this.service = MetadataFieldService;
    this.modal = $uibModalInstance;
    this.fields = fields;
    this.field = {};
    this.list = [];

    $scope.$watch(() => {
      return this.field;
    }, (newValue, oldValue) => {
      if (newValue.label !== oldValue.label) {
        this.modal.close(angular.copy(this.field));
      }
    });
  }

  queryName(query) {
    if (query.length > 2) {
      this
        .service
        .query({query})
        .$promise
        .then(results => {
          // Make sure the fields returned are not already in the table.
          const fields = filterFields(this.fields, results);

          // See if the query is in the list
          const found = fields
            .filter(field => field.label === query);
          if (found.length === 0) {
            fields.push({
              id: undefined,
              label: query,
              type: 'text'
            });
          }
          this.list = fields;
        });
    } else {
      this.list = [];
    }
  }
}

AddMetadataFieldController.$inject = [
  '$scope',
  '$uibModalInstance',
  'MetadataFieldService',
  'fields'
];

export function addMetadataField($uibModal) {
  return function(fields) {
    const options = {
      templateUrl: `addMetadataField.tmpl.html`,
      controllerAs: 'model',
      controller: AddMetadataFieldController,
      resolve: {
        fields() {
          return fields;
        }
      }
    };

    const modal = $uibModal.open(options);

    return modal.result;
  };
}
