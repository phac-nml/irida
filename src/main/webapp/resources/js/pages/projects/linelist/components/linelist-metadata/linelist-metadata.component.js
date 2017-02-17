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
  const ORIGINAL_ORDER = Array.from(vm.fields);

  vm.$onInit = () => {
    vm.selectedTemplate = vm.templates[0] || {};
  };

  vm.showMetadataTemplator = () => {
    $aside.open({
      templateUrl: asideTemplateUrl,
      openedClass: 'metadata-open',
      controllerAs: '$ctrl',
      controller(fields) {
        this.fields = fields;
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

  this.saveTemplate = () => {
    this.saving = true;

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
      })
      .finally(() => {
        this.saving = false;
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
          const oldCols = Array.from(vm.dtColumns);
          const newCols = [];

          columns.forEach(col => {
            for (let i = 0; i < oldCols.length; i++) {
              const c = oldCols[i];
              if (c.sTitle === col.label) {
                c.visible = true;
                newCols.push(c);
                oldCols.splice(i, 1);
                break;
              }
            }
          });
          vm.dtColumns = [...newCols, ...oldCols];
        });
    }
  };

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

controller.$inject = ['$scope', '$aside', '$uibModal'];

export const MetadataComponent = {
  templateUrl,
  require: {
    parent: '^^linelistTable'
  },
  bindings: {
    fields: '=',
    templates: '<',
    onSaveTemplate: '&',
    onGetTemplateFields: '&'
  },
  controller
};
