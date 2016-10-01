/**
 * @file ui.router state for setting the column header associated with the
 * sample identifier.
 */
//
// /**
//  * Controller for setting the sample id for the table.
//  * @param {object} $state ui.router state object.
//  * @param {object} $stateParams ui.router state parameters object.
//  * @constructor
//  */
// function SetSampleIdController($state, $stateParams) {
//   const vm = this;
//
//   if ($stateParams.headers.length === 0) {
//     $state.go('upload');
//   } else {
//     vm.headers = $stateParams.headers;
//
//     vm.setHeader = header => {
//       console.log(header);
//     };
//   }
// }

const sampleIdState = {
  name: "sampleId",
  url: "/sampleId",
  component: "setSampleId"
};

export default sampleIdState;
