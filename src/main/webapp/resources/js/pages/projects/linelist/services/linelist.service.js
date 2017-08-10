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
    // Get all the headers for the table (this is wrapped in the scroller).
    const trs = document.querySelectorAll(".dataTables_scrollHeadInner th");
    // Since this is a nodeList not an array, we need to convert it to an array
    // then map over the items and get the text.
    return [...trs].map(tr => tr.innerText);
  }
}

LinelistService.$inject = ["$window", "$http", "$q"];
