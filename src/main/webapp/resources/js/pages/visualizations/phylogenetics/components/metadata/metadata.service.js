import {MetadataManager} from './MetadataManager';
const _metadataManager = Symbol('metadata');

export class MetadataService {
  constructor($http, $rootScope) {
    this.$http = $http;
    this.$rootScope = $rootScope;
  }

  getMetadata(url) {
    return this.$http.get(url)
      .then(response => {
        this[_metadataManager] = new MetadataManager(
          response.data.terms,
          response.data.metadata);
        return {
          terms: response.data.terms,
          metadata: this[_metadataManager].getAllMetadata()
        };
      });
  }
}
