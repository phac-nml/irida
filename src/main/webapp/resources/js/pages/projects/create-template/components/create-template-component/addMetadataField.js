const angular = require('angular');

/**
 * Private function to remove fields that are already displayed
 * in the table of metadata fields for this template.
 * @param {Array} tableFields metadata fields already displayed in table
 * @param {Array} serverResults metadata fields available from server based on query.
 * @return {Array} list of new server defined metadata fields.
 */
function filterFields(tableFields, serverResults) {
  const serverFields = new Set(serverResults);
  tableFields.forEach(field => {
    for (let item of serverFields) {
      if (angular.equals(item, field)) {
        serverFields.delete(item);
        break;
      }
    }
  });
  return Array.from(serverFields);
}

class AddMetadataFieldController {
  constructor($scope, $uibModalInstance, MetadataFieldService, fields) {
    this.service = MetadataFieldService;
    this.modal = $uibModalInstance;
    this.tableFields = fields;
    this.field = {};
    this.list = [];

    $scope.$watch(() => {
      return this.field;
    }, (newValue, oldValue) => {
      if (newValue !== oldValue) {
        this.modal.close(angular.copy(this.field));
      }
    });
  }

  close() {
    this.modal.dismiss();
  }

  queryName(query) {
    if (query.length > 2) {
      // First check to see if the query is already in the table
      const matchesInTable = this.tableFields
        .filter(field => {
          return field.label.toLowerCase() === query.toLowerCase();
        });

      if (matchesInTable.length > 0) {
        // Display Message that fields already in the table
        this.fieldExists = true;
        this.list = [];
      } else {
        // Clear any warning message;
        this.fieldExists = false;

        this
          .service
          .query({query})
          .$promise
          .then(results => {
            // Make sure the fields returned are not already in the table.
            const availableFields = filterFields(this.tableFields, results);

            // See if the query is in the list
            const found = availableFields
              .filter(field => field === query);

            // Mark fields that aren't new
            availableFields.forEach(function(field, index) {
              availableFields[index] = {label: field, new: false};
            });

            // if it's a new field, mark it
            if (found.length === 0) {
              availableFields.push({label: query, new: true});
            }
            this.list = availableFields;
          });
      }
    }
  }
}

AddMetadataFieldController.$inject = [
  '$scope',
  '$uibModalInstance',
  'MetadataFieldService',
  'fields'
];

/**
 * Controller for displaying a modal for selection of metadata fields.
 * @param {object} $uibModal angular-ui bootstrap modal service.
 * @return {promise} A promise that the modal will sometimes be closed.
 */
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
