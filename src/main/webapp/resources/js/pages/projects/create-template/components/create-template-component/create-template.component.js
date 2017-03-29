class createTemplateController {
  constructor(SampleMetadataTemplateService, addMetadataField, notifications) {
    this.TemplateService = SampleMetadataTemplateService;
    this.addMetadataField = addMetadataField;
    this.notifications = notifications;

    // Initialize fields
    this.templates = [];
    this.fields = [];
    this.template = {};
  }

  $onInit() {
    // Get a list of the templates so that we can validate that the name is unique.
    this.TemplateService.query(templates => {
      this.templates = templates;
    });
  }

  saveTemplate() {
    const newTemplate = new this.TemplateService();
    newTemplate.name = this.template.name;
    newTemplate.fields = this.fields
      .map(field => field.label);
    newTemplate.$save(response => {
      this.notifications.show({
        type: 'success',
        msg: response.message
      });
    });
  }

  addNewField() {
    this
      .addMetadataField(this.fields)
      .then(field => {
        this.fields.push(field);
      });
  }

  removeField($index) {
    this.fields.splice($index, 1);
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
