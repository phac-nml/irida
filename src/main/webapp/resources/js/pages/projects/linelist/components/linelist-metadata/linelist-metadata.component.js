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
  const ORIGINAL_ORDER = Array.from(this.fields);
  this.showMetadataTemplator = () => {
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

  this.saveTemplate = () => {
    this.saving = true;

    const aside = $uibModal
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
      });

    aside
      .result
      .then(name => {
        saveTempalte(name);
      })
      .finally(() => {
        this.saving = false;
      });
  };

  function saveTempalte(templateName) {
    const fields = vm.fields
      .filter(field => field.selected)
      .map(field => field.text);

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
