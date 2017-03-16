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

  getHeaders() {
    return this.$window.headersList;
  }
}

LinelistService.$inject = [
  '$window',
  '$http',
  '$q'
];
