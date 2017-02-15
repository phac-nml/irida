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
  vm.selectedTemplate = vm.templates[0] || {};

  const ORIGINAL_ORDER = Array.from(vm.fields);
  vm.showMetadataTemplator = () => {
    $aside.open({
      templateUrl: asideTemplateUrl,
      openedClass: 'metadata-open',
      controllerAs: '$ctrl',
      controller(fields) {
        this.fields = fields;
        this.toggleField = field => {
          vm.onToggleField({
            $event: {
              field
            }
          });
        };
      },
      resolve: {
        fields() {
          return vm.fields;
        }
      },
      placement: 'left',
      size: 'sm'
    });
  };

  vm.templateSelected = event => {
    console.log(event);
  };

  vm.saveTemplate = () => {
    vm.saving = true;

    $uibModal
      .open({
        templateUrl: `save-template.tmpl.html`,
        controllerAs: '$modal',
        controller: function(templates, $uibModalInstance) {
          this.template = {};
          this.templates = [{name: 'fred'}, {name: 'johny'}];

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
    if (vm.selectedTemplate.id !== 'all') {
      vm.onGetTemplateFields({
        $event: {
          templateId: vm.selectedTemplate.id
        }
      })
        .then(columns => {
          console.log(this.fields, columns);
        });
    }
  };

  function saveTemplate(templateName) {
    const fields = vm.fields
      .filter(field => field.selected)
      .map(field => field.text);

    vm.onSaveTemplate({
      $event: {
        templateName,
        fields
      }
    }).then(result => {
      console.info(result);
    }, error => {
      console.error(error);
    }).then(() => {
      vm.saving = false;
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
    onSaveTemplate: '&',
    onGetTemplateFields: '&'
  },
  controller
};
