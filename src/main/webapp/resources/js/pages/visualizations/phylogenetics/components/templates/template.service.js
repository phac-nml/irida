export class TemplateService {
  constructor($http) {
    this.$http = $http;
  }

  getTemplates(url) {
    return this.$http.get(url).then(result => result.data.templates);
  }

  getFieldsForTemplate(url, templateId) {
    return this.$http
      .get(`${url}?templateId=${templateId}`)
      .then(result => result.data.fields);
  }
}

TemplateService.$inject = ["$http"];
