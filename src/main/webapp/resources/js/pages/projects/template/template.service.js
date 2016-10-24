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

  saveTemplate(template) {
    // return this.$http
    //   .post(`${this.url}/save-template`,
    //     {name: template.name, fields: JSON.stringify(template.fields)}
    //     );
    return $.ajax({
      contentType: 'application/json; charset=utf-8',
      type: 'POST',
      url: `${this.url}/save-template`,
      data: JSON.stringify(template),
      dataType: 'json'
    });
  }
}
