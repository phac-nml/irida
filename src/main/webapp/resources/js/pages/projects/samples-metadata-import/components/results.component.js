const resultsComponent = {
  templateUrl: 'results.component.tmpl.html',
  bindings: {
    data: '='
  },
  controller() {
    this.found = this.data.found.length;
    this.missing = this.data.missing.length;
  }
};

export default resultsComponent;
