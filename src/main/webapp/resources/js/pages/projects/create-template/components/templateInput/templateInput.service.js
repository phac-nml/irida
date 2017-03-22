const $ = require('jquery');
const angular = require('angular');

export class TemplateInputService {
  constructor($http, $window) {
    this.$http = $http;
    this.$window = $window;
  }
  saveTemplate(url, template, redirectUrl) {
    // Need to use jquery since I made a bad decision early on to wrap $http posts
    // to be form data :(
    return $.ajax({
      type: 'POST',
      contentType: 'application/json; charset=utf-8',
      url: `${url}/${template.name}`,
      data: angular.toJson(template.fields)
    }).done(response => {
      // After completion redirect to linelist page
      // displaying the newly created template.
      this.$window.location.href =
        `${redirectUrl}?templateId=${response.templateId}`;
    });
  }
  getMetadataFieldNames(query) {
    return this.$http
      .get(`/projects/4/sample-metadata/fields?query=${query}`)
      .then(results => results.data);
  }
}
