const $ = require('jquery');

export default class TemplateService {
  constructor($http, $window) {
    this.$http = $http;
    this.url = $window.location.href;
  }
  getFieldsForTemplates(template = '') {
    const data = $.param({template});
    return this.$http.get(`${this.url}/current?${data}`)
      .then(response => response.data);
  }
}
