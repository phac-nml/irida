import {EVENTS} from './../../constants';
const templateUrl = 'metadata.button.tmpl';
const asideTemplateUrl = 'metadata.aside.tmpl';

// This is all the headers in their original order when
// the datatable was created.
const FIELDS = window.headersList.map((header, index) => {
  return ({text: header, index, selected: true});
});

export const MetadataComponent = {
  templateUrl,
  controller: class MetadataComponent {
    constructor($scope, $aside) {
      this.displayFields = Object.assign(FIELDS);
      this.templates = window.templates;  // TODO:  get this from a service;
      this.selectedTemplate = '';
      this.$aside = $aside;
      this.$scope = $scope;

      // Set up event listener for re-arranging the columns on the table.
      this.$scope.$on(EVENTS.TABLE.colReorder, (e, args) => {
        const order = args.columns;
        if (order) {
          this.displayFields = order.map(originalIndex => {
            return FIELDS[originalIndex];
          });
        }
      });
    }

    showMetadataTemplator() {
      const vm = this;
      this.$aside.open({
        templateUrl: asideTemplateUrl,
        openedClass: 'metadata-open',
        controllerAs: '$ctrl',
        resolve: {
          fields() {
            return vm.displayFields;
          }
        },
        controller(fields) {
          this.fields = fields;
          this.toggleField = field => {
            vm.$scope.$parent.$ctrl.toggleFieldVisibility(field);
          };
        },
        placement: 'left',
        size: 'sm'
      });
    }

    templateSelected(event) {
      console.log(event);
    }

    saveTemplate() {
      const fields = this.displayFields
        .filter(field => field.selected);
      console.log('These fields need to be saved: ', fields);
    }
  }
};
