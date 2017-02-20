import {EVENTS} from './../../constants';

/**
 * Controller for MetadataComponent. Handles displaying toggles
 * for hiding and showing metadata columns.
 *
 * @param {object} $scope angular DOM scope reference.
 * @param {object} $aside Reference to the angular-aside instance
 * @param {object} $uibModal Reference to the angular-bootstrap modal instance
 *
 * @description
 *
 */
function MetadataController($scope, $aside, $uibModal) {
  const vm = this;
  const ORIGINAL_ORDER = Array.from(vm.fields);

  vm.$onInit = () => {
    vm.selectedTemplate = vm.templates[vm.activeTemplate];
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
    this.saving = true;

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
      })
      .finally(() => {
        this.saving = false;
      });
  };

  vm.templateSelected = () => {
    if (vm.selectedTemplate.id === 'all') {
      vm.fields.forEach(field => {
        field.visible = true;
      });
    } else {
      vm.onGetTemplateFields({
        $event: {
          templateId: vm.selectedTemplate.id
        }
      })
        .then(columns => {
          vm.fields = columns;
        });
    }
  };

  /**
   * Save the currently visible fields as a MetadataTemplate
   * @param {string} templateName name for the new template
   */
  function saveTemplate(templateName) {
    const fields = vm.fields
      .filter(field => field.visible)
      .map(field => field.sTitle);

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

MetadataController.$inject = ['$scope', '$aside', '$uibModal'];

export const MetadataComponent = {
  templateUrl: 'metadata.button.tmpl',
  require: {
    parent: '^^linelistTable'
  },
  bindings: {
    fields: '=',
    templates: '<',
    onSaveTemplate: '&',
    onGetTemplateFields: '&',
    activeTemplate: '<'
  },
  controller: MetadataController
};
