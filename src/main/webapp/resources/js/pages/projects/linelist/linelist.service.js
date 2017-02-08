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

  saveTemplate({url, name, fields}) {
    return this.$http
      .post(url, {
        fields,
        name
      });
  }
}

LinelistService.$inject = ['$window', '$http', '$q'];
