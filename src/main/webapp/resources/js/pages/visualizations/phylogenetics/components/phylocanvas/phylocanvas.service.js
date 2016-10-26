export class PhylocanvasService {
  constructor($http) {
    this.$http = $http;
  }

  getNewickData(url) {
    return this.$http.get(url)
      .then(response => {
        return response.data.newick;
      });
  }
}
