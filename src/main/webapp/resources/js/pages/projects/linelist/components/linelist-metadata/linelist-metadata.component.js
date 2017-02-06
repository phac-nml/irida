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
  this.$onInit = () => {
    this.fields = this.headers
      .map((header, index) => {
        return ({text: header, index, selected: true});
      });
    delete this.headers;
  };

  this.showMetadataTemplator = () => {
    $aside.open({
      templateUrl: asideTemplateUrl,
      openedClass: 'metadata-open',
      controllerAs: '$ctrl',
      controller() {
        this.fields = Object.assign(vm.fields);
        this.toggleField = field => {
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

  this.templateSelected = event => {
    console.log(event);
  };

  this.saveTemplate = () => {
    const fields = this.displayFields
      .filter(field => field.selected);
    console.log('These fields need to be saved: ', fields);
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
    headers: '<',
    onToggleField: '&'
  },
  controller
};
