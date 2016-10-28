import {METADATA} from './../../constants';
import {formatMetadata} from './metadataFormatter';

export class MetadataService {
  constructor($http, $rootScope) {
    this.$http = $http;
    this.$rootScope = $rootScope;
  }

  getMetadata(url, template = 'default') {
    return this.$http.get(`${url}?template=${template}`)
      .then(response => {
        const metadata = formatMetadata(response.data.metadata);
        this.$rootScope.$broadcast(METADATA.UPDATED, {metadata});
      });
  }
}
