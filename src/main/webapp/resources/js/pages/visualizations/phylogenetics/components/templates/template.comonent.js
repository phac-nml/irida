import { METADATA, TEMPLATES } from "./../../constants";

/**
 * Controller for the TemplateComponent
 * @param {object} $rootScope angular root scope
 * @param {object} TemplateService angular service
 */
function templateController($rootScope, TemplateService) {
  this.$onInit = () => {
    // Get a list of templates that can be rendered
    this.templates = [];
    TemplateService.getTemplates(this.templatesurl).then(
      templates => {
        this.templates = templates;
      },
      () => {
        // This will be hit if some of the samples in this analysis
        // are no longer in the project, therefore the analysis
        // will need to be re-run without.
        $rootScope.$broadcast(TEMPLATES.ERROR);
      }
    );
  };

  this.templateChange = () => {
    if (this.selectedTemplate) {
      TemplateService.getFieldsForTemplate(
        this.fieldsurl,
        this.selectedTemplate
      ).then(fields => $rootScope.$broadcast(METADATA.TEMPLATE, { fields }));
    } else {
      $rootScope.$broadcast(METADATA.TEMPLATE, METADATA.ALL_FIELDS);
    }
  };
}

templateController.$inject = ["$rootScope", "TemplateService"];

export const TemplateComponent = {
  bindings: {
    templatesurl: "@",
    fieldsurl: "@"
  },
  templateUrl: "template-component.tmpl.html",
  controller: templateController
};
