export class LinelistTableService {
  constructor($q) {
    this.$q = $q;
  }

  getMetadata() {
    const defer = this.$q.defer();
    defer.resolve(window.metadataList);
    return defer.promise;
  }

  getColumns() {
    return window.headersList;
  }
}
