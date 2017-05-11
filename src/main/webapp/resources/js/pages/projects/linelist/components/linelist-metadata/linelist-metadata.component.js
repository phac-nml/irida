import {EVENTS} from './../../constants';

/**
 * Controller for handling getting the name for a new template.
 * @param {array} templates of existing templates.
 * @param {object} $uibModalInstance handle on the current modal.
 */
function saveTemplateController(templates, $uibModalInstance) {
  this.templates = templates;
  this.template = {};

  this.cancel = () => {
    this.template.name = '';
    $uibModalInstance.dismiss();
  };

  this.save = () => {
    $uibModalInstance.close(this.template.name);
    this.template.name = '';
  };
}

saveTemplateController.$inject = [
  'templates',
  '$uibModalInstance'
];

/**
 * Controller for the ng-aside that allows the user to select
 * visible metadata fields
 * @param {object} $uibModalInstance angular-ui modal instance.
 * @param {array} fields metadata fields
 * @param {function} toggleColumnVisibility call to toggle the column within the Datatables.
 */
function showMetadataFieldSelectionsController($uibModalInstance, fields,
                                               toggleColumnVisibility) {
  this.fields = fields;

  this.toggleColumn = column => {
    toggleColumnVisibility(column);
  };

  this.close = () => {
    $uibModalInstance.dismiss();
  };
}

showMetadataFieldSelectionsController.$inject = [
  '$uibModalInstance',
  'fields',
  'toggleColumnVisibility'
];

/**
 * Controller for MetadataComponent. Handles displaying toggles
 * for hiding and showing metadata columns.
 *
 * @param {object} $window angular window reference.
 * @param {object} $scope angular DOM scope reference.
 * @param {object} $aside Reference to the angular-aside instance
 * @param {object} $uibModal Reference to the angular-bootstrap modal instance
 * @param {object} MetadataTemplateService service for handling metadata templates
 * @param {object} notifications IRIDA browser notification service.
 *
 * @description
 *
 */
function MetadataController($window, $scope, $aside, $uibModal,
                            MetadataTemplateService, notifications) {
  const vm = this;
  let FIELD_ORDER;
  let FIELDS;
  vm.headers = [];

  /**
   * Angular controller initialization block.
   *  - Getting the templates for this project
   *  - Setting up the metadata fields for toggling columns on the Datatables.
   */
  vm.$onInit = () => {
    MetadataTemplateService.query(templates => {
      this.templates = templates;
      // Add a fake template so the user can see all the fields.
      this.templates.unshift(
        {
          label: $window.PAGE.i18n.allFields,
          identifier: 0,
          fields: []
        }
      );
      this.selectedTemplate = this.templates[0];
    });

    FIELDS = vm.fields
      .map(field => {
        return {label: field, visible: true};
      });
    FIELD_ORDER = vm.fields.map((x, i) => i);
  };

  /**
   * EventHandler for a click to open the metadata selection side panel.
   */
  vm.showMetadataTemplator = () => {
    $aside.open({
      templateUrl: 'metadata.aside.tmpl',
      openedClass: 'metadata-open',
      controllerAs: '$ctrl',
      controller: showMetadataFieldSelectionsController,
      resolve: {
        fields() {
          // Send in the appropriately ordered fields based
          // on the current table order.
          return FIELD_ORDER.map(col => {
            return FIELDS[col];
          });
        },
        toggleColumnVisibility() {
          return vm.parent.updateColumnVisibility;
        }
      },
      placement: 'left',
      size: 'sm'
    });
  };

  /**
   * EventHandler for a click on the button to save a new MetadataTemplate
   */
  vm.saveTemplate = () => {
    $uibModal
      .open({
        templateUrl: `save-template.tmpl.html`,
        controllerAs: '$modal',
        controller: saveTemplateController,
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

  /**
   * EventHandler for a selection of a MetadataTemplate to use.
   */
  vm.templateSelected = () => {
    let fields;
    // "All Fields" is set to have an identifier of 0.
    if (Number(vm.selectedTemplate.identifier) === 0) {
      // Make sure that all fields are visible
      FIELDS.forEach(field => {
        field.visible = true;
      });
      fields = Array.from(FIELDS);

      // Datatables does not fire an event when the table is reset to its
      // original state, so we will just reset the field order here.
      FIELD_ORDER = vm.fields.map((x, i) => i);
    } else {
      fields = Array.from(vm.selectedTemplate.fields);

      // Make sure only the headers visible are turned on.
      FIELDS.forEach(field => {
        const index = fields.findIndex(f => {
          return f.label === field.label;
        });
        field.visible = index > -1;
      });
    }
    // Update the Datatables with the fields to display.
    vm.parent.templateSelected(fields);
  };

  /**
   * Save the currently visible fields as a MetadataTemplate
   * @param {string} name for the new template
   */
  function saveTemplate(name) {
    const fields = FIELD_ORDER
      .map(index => {
        // Need to put the fields in the order they are in the table
        return FIELDS[index];
      })
      .filter(field => field.visible)
      .map(field => field.label);
    const newTemplate = new MetadataTemplateService();
    newTemplate.name = name;
    newTemplate.fields = fields;
    newTemplate.$save(response => {
      const {template, message} = response;
      vm.templates.push(template);
      vm.selectedTemplate = template;
      notifications.show({
        msg: message,
        type: 'success'
      });
    });
  }

  // Set up event listener for re-arranging the columns on the table.
  $scope.$on(EVENTS.TABLE.colReorder, (e, args) => {
    const newOrder = args.columns;
    if (newOrder) {
      FIELD_ORDER = newOrder;
    }
  });
}

MetadataController.$inject = [
  '$window',
  '$scope',
  '$aside',
  '$uibModal',
  'SampleMetadataTemplateService',
  'notifications'
];

export const MetadataComponent = {
  templateUrl: 'metadata.button.tmpl',
  require: {
    parent: '^^linelist'
  },
  bindings: {
    templates: '<',
    fields: '<'
  },
  controller: MetadataController
};
