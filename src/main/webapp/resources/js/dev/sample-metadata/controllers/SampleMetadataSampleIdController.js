/**
 * AngularJS Controller for the Sample Metadata Sample Id Selection
 * @param {object} $stateParams ui.router state params object
 * @constructor
 */
export default function SampleMetadataSampleIdController($stateParams) {
  const vm = this;

  vm.headers = $stateParams.headers;
}
