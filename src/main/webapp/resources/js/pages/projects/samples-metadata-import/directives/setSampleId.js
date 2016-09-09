/**
 * @file AngularJS Directive for setting the column header that
 * corresponds to a sample identifier.
 */
const bindToController = {
  url: "@",
  heading: "@"
};

const template = `
<div class="panel panel-default">
  <div class="panel-heading">
    <h2 class="panel-title" ng-bind="setCtrl.heading"></h2>
  </div>
  <div class="panel-body">
    <div class="radio" ng-repeat="header in setCtrl.headers">
      <label>
        <input type="radio"
               ng-model="setCtrl.selectedColumn"
               ng-value="header"/> {{ header }} 
      </label>
    </div>
  </div>
  <div class="panel-footer">
    <button class="btn btn-primary" 
            ng-click="setCtrl.complete()">Next</button>
  </div>
</div>
`;

/**
 * Controller for setting the sample id.
 * @param {object} $state ui.router state object.
 * @param {object} $stateParams ui.router state parameter object
 * @param {object} sampleMetadataService service to handle server calls
 * for metadata
 * @constructor
 */
function SetSampleIdController($state, $stateParams, sampleMetadataService) {
  const vm = this;
  if ($stateParams.headers.length === 0) {
    $state.go("upload");
  }

  vm.headers = $stateParams.headers;
  vm.selectedColumn = vm.headers[0];

  vm.complete = () => {
    sampleMetadataService.setSampleIdColumn(vm.url, vm.selectedColumn)
      .then(result => {
        result.headers = vm.headers;
        $state.go("results", result);
      });
  };
}

const setSampleId = () => {
  return {
    bindToController,
    template,
    replace: true,
    restrict: "E",
    controllerAs: "setCtrl",
    controller: ["$state", "$stateParams", "sampleMetadataService",
      SetSampleIdController]
  };
};

export default setSampleId;
