export class LinelistService {
  constructor($window, $http, $q) {
    this.$window = $window;
    this.$http = $http;
    this.$q = $q;
  }

  getMetadata() {
    const defer = this.$q.defer();
    defer.resolve(this.$window.metadataList);
    return defer.promise;
  }

  getColumns() {
    return this.$window.headersList;
  }

  getTemplates() {
    return this.$window.templates;
  }

  getTemplateFields({url, templateId}) {
    return this
      .$http({
        method: 'GET',
        url,
        params: {templateId}
      })
      .then(response => response.data);
  }

  saveTemplate({url, name, fields}) {
    return this
      .$http
      .post(url, {
        fields,
        name
      });
  }
}

LinelistService.$inject = ['$window', '$http', '$q'];
