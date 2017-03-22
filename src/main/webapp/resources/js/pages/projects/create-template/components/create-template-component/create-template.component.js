class createTemplateController {
  constructor(SampleMetadataTemplateService, notifications) {
    this.TemplateService = SampleMetadataTemplateService;
    this.notifications = notifications;
    this.templates = [];
  }
  $onInit() {
    this.TemplateService.query(templates => {
      this.templates = templates;
    });
    this.template = {};
  }
  saveTemplate() {
    const newTemplate = new this.TemplateService();
    newTemplate.name = this.template.name;
    newTemplate.fields = this.template.fields || ['Fred', 'money'];
    newTemplate.$save(template => {
      this.notifications.show({
        type: 'success',
        msg: `Metadata template ${template.label} successfully created.`
      });
    });
  }
}

createTemplateController.$inject = [
  'SampleMetadataTemplateService',
  'notifications'
];

export const createTemplate = {
  templateUrl: `create-template.tmpl.html`,
  controller: createTemplateController
};
