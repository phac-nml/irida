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

  this.templateSelected = event => {
    console.log(event);
  };

  this.saveTemplate = () => {
    this.saving = true;

    $uibModal
      .open({
        templateUrl: `save-template.tmpl.html`,
        controllerAs: '$modal',
        controller: function(templates) {
          this.template = {};

          // Create a validator for the tamplate name field
          this.$validators.templateName

          console.log(templates);
          this.checkName = () => {
            templates
              .filter(template => this.template.name === template);
          };
        },
        resolve: {
          templates: () => {
            return vm.templates;
          }
        }
      });
    // const fields = this.fields
    //   .filter(field => field.selected);
    // vm.onSaveTemplate({
    //   $event: {
    //     fields
    //   }
    // }).then(result => {
    //   console.info(result);
    // }, error => {
    //   console.error(error);
    // }).then(() => {
    //   this.saving = false;
    // });
  };

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
