import {METADATA} from './../../constants';
/**
 * Controller for the TemplateComponent
 * @param {object} $rootScope angular root scope
 * @param {object} TemplateService angular service
 */
function controller($rootScope, TemplateService) {
  this.$onInit = () => {  // Get a list of templates that can be rendered
    this.templates = [];
    TemplateService
      .getTemplates(this.templatesurl)
      .then(templates => {
        this.templates = templates;
      });
  };

  this.templateChange = () => {
    if (this.selectedTemplate) {
      TemplateService
        .getFieldsForTemplate(this.fieldsurl, this.selectedTemplate)
        .then(fields => $rootScope.$broadcast(METADATA.TEMPLATE, {fields}));
    } else {
      $rootScope.$broadcast(METADATA.TEMPLATE, METADATA.ALL_FIELDS);
    }
  };
}

export const TemplateComponent = {
  bindings: {
    templatesurl: '@',
    fieldsurl: '@'
  },
  templateUrl: 'template-component.tmpl.html',
  controller
};
