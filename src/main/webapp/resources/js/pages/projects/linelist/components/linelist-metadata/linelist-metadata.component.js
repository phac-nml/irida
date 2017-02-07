import {EVENTS} from './../../constants';
const templateUrl = 'metadata.button.tmpl';
const asideTemplateUrl = 'metadata.aside.tmpl';

/**
 * Controller for MetadataComponent. Handles displaying toggles
 * for hiding and showing metadata columns,
 * @param {object} $scope angular DOM scope reference.
 * @param {object} $aside Reference to the angular-aside instance
 */
function controller($scope, $aside) {
  const vm = this;
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
    const fields = this.fields
      .filter(field => field.selected);
    vm.onSaveTemplate({
      $event: {
        fields
      }
    });
  };

  // Set up event listener for re-arranging the columns on the table.
  $scope.$on(EVENTS.TABLE.colReorder, (e, args) => {
    const order = args.columns;
    if (order) {
      this.displayFields = order.map(originalIndex => {
        return this.fields[originalIndex];
      });
    }
  });
}

controller.$inject = ['$scope', '$aside'];

export const MetadataComponent = {
  templateUrl,
  require: {
    parent: '^^linelistTable'
  },
  bindings: {
    fields: '=',
    onToggleField: '&',
    onSaveTemplate: '&'
  },
  controller
};
