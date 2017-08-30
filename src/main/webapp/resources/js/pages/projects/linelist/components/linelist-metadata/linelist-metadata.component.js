import { EVENTS } from "./../../constants";

/**
 * Controller for handling getting the name for a new template.
 * @param {array} templates of existing templates.
 * @param {object} $uibModalInstance handle on the current modal.
 */
function saveTemplateController(templates, $uibModalInstance) {
  this.templates = templates;
  this.template = {};

  this.cancel = () => {
    this.template.name = "";
    $uibModalInstance.dismiss();
  };

  this.save = () => {
    $uibModalInstance.close(this.template.name);
    this.template.name = "";
  };
}

saveTemplateController.$inject = ["templates", "$uibModalInstance"];

/**
 * Controller for the ng-aside that allows the user to select
 * visible metadata fields
 * @param {object} $rootScope angular highest level scope object.
 * @param {object} $uibModalInstance angular UI modal instance.
 * @param {array} fields metadata fields
 */
function showMetadataFieldSelectionsController(
  $rootScope,
  $uibModalInstance,
  fields
) {
  this.fields = fields;

  this.toggleColumn = column => {
    const index = fields.findIndex(field => field.label === column.label);
    $rootScope.$broadcast(EVENTS.TABLE.columnVisibility, { column, index });
  };

  this.close = () => {
    $uibModalInstance.dismiss();
  };
}

showMetadataFieldSelectionsController.$inject = [
  "$rootScope",
  "$uibModalInstance",
  "fields"
];

/**
 * Controller for MetadataComponent. Handles displaying toggles
 * for hiding and showing metadata columns.
 *
 * @param {object} $window angular window reference.
 * @param {object} $rootScope angular highest level scope object.
 * @param {object} $scope angular DOM scope reference.
 * @param {object} $aside Reference to the angular-aside instance
 * @param {object} $uibModal Reference to the angular-bootstrap modal instance
 * @param {object} MetadataTemplateService service for handling metadata templates
 * @param {object} notifications IRIDA browser notification service.
 *
 * @description
 *
 */
function MetadataController(
  $window,
  $rootScope,
  $scope,
  $aside,
  $uibModal,
  MetadataTemplateService,
  notifications
) {
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
      this.templates.unshift({
        label: $window.PAGE.i18n.allFields,
        identifier: 0,
        fields: []
      });
      this.selectedTemplate = this.templates[0];
    });

    FIELDS = vm.fields.map(field => {
      return { label: field, visible: true };
    });
    FIELD_ORDER = vm.fields.map((x, i) => i);
  };

  /**
   * EventHandler for a click to open the metadata selection side panel.
   */
  vm.showMetadataTemplator = () => {
    $aside.open({
      templateUrl: "metadata.aside.tmpl",
      openedClass: "metadata-open",
      controllerAs: "$ctrl",
      controller: showMetadataFieldSelectionsController,
      resolve: {
        fields() {
          // Send in the appropriately ordered fields based
          // on the current table order.
          const fields = FIELD_ORDER.map(col => {
            return FIELDS[col];
          });
          // Remove the label
          fields.shift();
          return fields;
        }
      },
      placement: "left",
      size: "sm"
    });
  };

  /**
   * EventHandler for a click on the button to save a new MetadataTemplate
   */
  vm.saveTemplate = () => {
    $uibModal
      .open({
        templateUrl: `save-template.tmpl.html`,
        controllerAs: "$modal",
        controller: saveTemplateController,
        resolve: {
          templates: () => {
            return vm.templates;
          }
        }
      })
      .result.then(name => {
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
      $rootScope.$broadcast(EVENTS.TABLE.reset);
    } else {
      fields = Array.from(vm.selectedTemplate.fields);
      // Make sure only the headers visible are turned on.
      FIELDS.forEach(field => {
        const index = fields.findIndex(f => {
          return f.label === field.label;
        });
        field.visible = index > -1;
      });
      // Update the Datatables with the fields to display.
      $rootScope.$broadcast(EVENTS.TABLE.template, { fields });
    }
  };

  /**
   * Save the currently visible fields as a MetadataTemplate
   * @param {string} name for the new template
   */
  function saveTemplate(name) {
    const fields = [
      ...document.querySelectorAll(".dataTables_scrollHeadInner th")
    ].map(th => th.innerText);
    fields.shift(); // Need to remove the sample name column.
    const newTemplate = new MetadataTemplateService();
    newTemplate.name = name;
    newTemplate.fields = fields;
    newTemplate.$save(response => {
      const { template, message } = response;
      vm.templates.push(template);
      vm.selectedTemplate = template;
      notifications.show({
        msg: message,
        type: "success"
      });
    });
  }

  // Set up event listener for re-arranging the columns on the table.
  $scope.$on(EVENTS.TABLE.colReorder, (e, args) => {
    // Order is a rearrangement of the current order.
    const { order } = args;
    if (order) {
      FIELD_ORDER = order;
    }
  });
}

MetadataController.$inject = [
  "$window",
  "$rootScope",
  "$scope",
  "$aside",
  "$uibModal",
  "SampleMetadataTemplateService",
  "notifications"
];

export const MetadataComponent = {
  templateUrl: "metadata.button.tmpl",
  require: {
    parent: "^^linelist"
  },
  bindings: {
    templates: "<",
    fields: "<"
  },
  controller: MetadataController
};
