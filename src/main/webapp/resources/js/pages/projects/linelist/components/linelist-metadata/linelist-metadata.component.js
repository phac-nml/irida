import {EVENTS} from './../../constants';
const templateUrl = 'metadata.button.tmpl';
const asideTemplateUrl = 'metadata.aside.tmpl';

/**
 * Controller for MetadataComponent. Handles displaying toggles
 * for hiding and showing metadata columns,
 * @param {object} $scope angular DOM scope reference.
 * @param {object} $aside Reference to the angular-aside instance
 * @param {object} $uibModal Reference to the angular-bootstrap modal instance
 */
function controller($scope, $aside, $uibModal) {
  const vm = this;
  let ORIGINAL_ORDER = [];

  vm.$onInit = () => {
    ORIGINAL_ORDER = Array.from(this.fields);
  };
  this.showMetadataTemplator = () => {
    // Open side panel to display the column headers for toggling column visibility.
    $aside.open({
      templateUrl: asideTemplateUrl,
      openedClass: 'metadata-open',
      controllerAs: '$ctrl',
      controller() {
        this.fields = vm.fields;

        /**
         * UI event handler for toggling the visibility one of the table columns.
         * @param {object} field the field to toggle.
         */
        this.toggleField = field => {
          // Tell the parent controller to toggle the columns on the table.
          vm.onToggleField({
            $event: {
              field
            }
          });
        };
      },
      placement: 'left',
      size: 'sm'
    });
  };

  /**
   * UI event handling for saving the currently visible columns as a MetadataTemplate
   */
  this.saveTemplate = () => {
    this.saving = true;

    // Open a uiModal to get a name for this template.
    $uibModal
      .open({
        templateUrl: `save-template.tmpl.html`,
        controllerAs: '$modal',
        controller: function(templates, $uibModalInstance) {
          this.template = templates || {};

          this.cancel = () => {
            $uibModalInstance.dismiss();
          };

          this.save = () => {
            $uibModalInstance.close(this.template.name);
          };
        },
        resolve: {
          templates: () => {
            return vm.templates;
          }
        }
      })
      .result
      .then(name => {
        saveTemplate(name);
      })
      .finally(() => {
        this.saving = false;
      });
  };

  /**
   * Save the currently visible columns and their order as a MetadataTemplate.
   * @param {string} templateName name of the template to create.
   */
  function saveTemplate(templateName) {
    // Get the names of the visible columns
    const fields = vm.fields
      .filter(field => field.selected)
      .map(field => field.text);

    // This action is handled by the table so pass it up to it.
    vm.onSaveTemplate({
      $event: {
        templateName,
        fields
      }
    }).then(result => {
      // TODO: (Josh | 2017-02-15) Handled in next merge request
      console.info(result);
    }, error => {
      // TODO: (Josh | 2017-02-15) Handled in next merge request.
      console.error(error);
    });
  }

  // Set up event listener for re-arranging the columns on the table.
  $scope.$on(EVENTS.TABLE.colReorder, (e, args) => {
    const order = args.columns;
    if (order) {
      this.fields = order.map(originalIndex => {
        return ORIGINAL_ORDER[originalIndex];
      });
    }
  });
}

controller.$inject = ['$scope', '$aside', '$uibModal'];

export const MetadataComponent = {
  templateUrl,
  require: {
    parent: '^^linelistTable'
  },
  bindings: {
    fields: '=',
    templates: '<',
    onToggleField: '&',
    onSaveTemplate: '&'
  },
  controller
};
