import {MetadataManager} from './MetadataManager';
import {METADATA} from './../../constants';
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

  getMetadataForKeys(keys) {
    const metadata = this[_metadataManager].getMetadataForKeys(keys);
    this.$rootScope.$broadcast(METADATA.UPDATED, {metadata});
  }
}
