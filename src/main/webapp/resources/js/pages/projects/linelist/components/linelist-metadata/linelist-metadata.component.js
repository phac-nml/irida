import {EVENTS} from './../../constants';

/**
 * Controller for MetadataComponent. Handles displaying toggles
 * for hiding and showing metadata columns.
 *
 * @param {object} $scope angular DOM scope reference.
 * @param {object} MetadataService angular service for metadata.
 * @param {object} $aside Reference to the angular-aside instance
 * @param {object} $uibModal Reference to the angular-bootstrap modal instance
 *
 * @description
 *
 */
function MetadataController($scope, MetadataService, $aside, $uibModal) {
  const vm = this;
  vm.tempaltes = [];

  vm.$onInit = () => {
    MetadataService.query(function(response) {
      vm.templates = response;
      response[0].fields = Array.from(vm.fields);
    });
  };

  vm.showMetadataTemplator = () => {
    $aside.open({
      templateUrl: 'metadata.aside.tmpl',
      openedClass: 'metadata-open',
      controllerAs: '$ctrl',
      controller() {
        this.fields = vm.fields;
      },
      placement: 'left',
      size: 'sm'
    });
  };

  this.saveTemplate = () => {
    $uibModal
      .open({
        templateUrl: `save-template.tmpl.html`,
        controllerAs: '$modal',
        controller: function(templates, $uibModalInstance) {
          this.template = templates;

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
      });
  };

  vm.templateSelected = () => {
    vm.fields = vm.onTemplateSelected({
      $event: {
        fields: vm.selectedTemplate.fields
      }
    });
  };

  /**
   * Save the currently visible fields as a MetadataTemplate
   * @param {string} templateName name for the new template
   */
  function saveTemplate(templateName) {
    const fields = vm.fields
      .filter(field => field.visible)
      .map(field => field.sTitle);

    const newTemplate = new MetadataService();
    newTemplate.name = templateName;
    newTemplate.fields = fields;
    newTemplate.$save();
    vm.templates.push(newTemplate);
    vm.selectedTemplate = newTemplate;
  }

  // Set up event listener for re-arranging the columns on the table.
  $scope.$on(EVENTS.TABLE.colReorder, (e, args) => {
    const order = args.columns;
    if (order) {
      const original = vm.templates[0].fields;
      this.fields = order.map(originalIndex => {
        return original[originalIndex];
      });
    }
  });
}

MetadataController.$inject = ['$scope', 'MetadataService', '$aside', '$uibModal'];

export const MetadataComponent = {
  templateUrl: 'metadata.button.tmpl',
  require: {
    parent: '^^linelist'
  },
  bindings: {
    fields: '=',
    templates: '<',
    onSaveTemplate: '&',
    onTemplateSelected: '&',
    activeTemplate: '<'
  },
  controller: MetadataController
};
