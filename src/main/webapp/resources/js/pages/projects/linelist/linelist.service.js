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
}

LinelistService.$inject = ['$window', '$http', '$q'];
