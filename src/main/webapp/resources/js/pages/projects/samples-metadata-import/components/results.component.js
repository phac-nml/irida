/**
 * @file AngularJS component to display all the results after
 * the sample name column has been selected.
 */
const resultsComponent = {
  templateUrl: "results.component.tmpl.html",
  bindings: {
    data: "="
  },
  controller() {
    this.$onInit = () => {
      this.found = this.data.found.length;
      this.missing = this.data.missing.length;
    };
  }
};

export default resultsComponent;
