export class LinelistService {
  constructor($window, $q) {
    this.$window = $window;
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
}

LinelistService.$inject = ['$window', '$q'];
