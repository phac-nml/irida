const angular = require('angular');

class createTemplateController {
  constructor(SampleMetadataTemplateService,
              addMetadataField, notifications) {
    const params = new URLSearchParams(window.location.search);
    if (params.has('templateId')) {
      this.templateId = params.get('templateId');
    }
    this.TemplateService = SampleMetadataTemplateService;
    this.addMetadataField = addMetadataField;
    this.notifications = notifications;

    // Initialize fields
    this.hideAdvanced = true; // This hides the delete button
    this.templates = [];
    this.template = {fields: []};
  }

  $onInit() {
    // Get a list of the templates so that we can validate that the name is unique.
    this.TemplateService.query(templates => {
      this.templates = templates;

      if (this.templateId) {
        const index = this.templates.findIndex(template => {
          return template.identifier === this.templateId;
        });
        this.template = angular.copy(this.templates[index]);
        this.templates.splice(index, 1);
      }
    });
  }

  saveTemplate() {
    const newTemplate = new this.TemplateService();
    newTemplate.name = this.template.name;
    newTemplate.fields = this.template.fields
      .map(field => field.label);
    newTemplate.$save(response => {
      this.template = response.template;
      this.notifications.show({
        type: 'success',
        msg: response.message
      });
    });
  }

  addNewField() {
    this
      .addMetadataField(this.template.fields)
      .then(field => {
        this.template.fields.push(field);
      });
  }

  removeField($index) {
    this.template.fields.splice($index, 1);
  }

  deleteTemplate() {
    // TODO: implement this.
    console.warn('Delete template methods needs to be implmented');
  }
}

createTemplateController.$inject = [
  'SampleMetadataTemplateService',
  'addMetadataField',
  'notifications'
];

export const createTemplate = {
  templateUrl: `create-template.tmpl.html`,
  controller: createTemplateController
};
