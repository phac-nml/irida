export class PhylocanvasService {
  constructor($http) {
    this.$http = $http;
  }

  getNewickData(url) {
    return this.$http.get(url)
      .then(response => response.data.newick);
  }

  getMetadata(url, template = 'default') {
    return this.$http.get(`${url}?template=${template}`)
      .then(response => response.data.metadata);
  }
}
