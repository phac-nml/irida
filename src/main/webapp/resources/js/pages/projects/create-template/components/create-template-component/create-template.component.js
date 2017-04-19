const angular = require('angular');

let original = {};
class createTemplateController {
  constructor(SampleMetadataTemplateService, deleteTemplate,
              addMetadataField, notifications) {
    const params = new URLSearchParams(window.location.search);
    if (params.has('templateId')) {
      this.templateId = params.get('templateId');
    }
    this.TemplateService = SampleMetadataTemplateService;
    this.deleteTemplate = deleteTemplate;
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
        if (index > -1) {
          this.template = angular.copy(this.templates[index]);
          this.templates.splice(index, 1);
          original = angular.copy(this.template);
        }
      }
    });
  }

  disableSave() {
    return (this.createTemplateForm.$invalid ||
      this.template.fields.length === 0) ||
      !this.templateIsModified();
  }

  templateIsModified() {
    return !angular.equals(original, this.template);
  }

  saveTemplate() {
    const newTemplate = new this.TemplateService();
    newTemplate.name = this.template.name;
    newTemplate.templateId = this.template.identifier;
    newTemplate.fields = this.template.fields
      .map(field => field.label);
    newTemplate.$save(response => {
      this.template = new this.TemplateService(response.template);
      original = angular.copy(this.template);
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
        this.template.fields.push({label: field.label});
      });
  }

  removeField($index) {
    this.template.fields.splice($index, 1);
  }

  removeTemplate() {
    this.deleteTemplate(this.template)
      .then(() => {
        this.template
          .$delete(
            {id: this.template.identifier},
            response => {
              this.template = {fields: []};
              this.createTemplateForm.$setPristine();
              this.notifications.show({
                type: 'success',
                msg: response.message
              });
            });
      }, () => {
        console.log('Not deleted.');
      });
  }
}

createTemplateController.$inject = [
  'SampleMetadataTemplateService',
  'deleteTemplate',
  'addMetadataField',
  'notifications'
];

export const createTemplate = {
  templateUrl: `create-template.tmpl.html`,
  controller: createTemplateController
};
