class createTemplateController {
  constructor(SampleMetadataTemplateService, addMetadataField, notifications) {
    this.TemplateService = SampleMetadataTemplateService;
    this.addMetadataField = addMetadataField;
    this.notifications = notifications;
  }
  $onInit() {
    this.templates = [];
    this.fields = [];
    this.template = {};
    this.TemplateService.query(templates => {
      this.templates = templates;
    });
  }
  saveTemplate() {
    const newTemplate = new this.TemplateService();
    newTemplate.name = this.template.name;
    newTemplate.fields = this.template.fields || ['Fred', 'money'];
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
