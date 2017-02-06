export class MetadataService {
  constructor($window) {
    this.$window = $window;
  }

  getMetadataFields() {
    return this.$window.headersList;
  }
}

MetadataService.$inject = ['$window'];
