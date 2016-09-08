import SampleMetadataUploaderController
  from "./controllers/SampleMetadataUploaderController";

export const states = ($stateProvider, $urlRouterProvider) => {
  $stateProvider
    .state('metadata', {
      url: "/start",
      templateUrl: "upload.tmpl.html",
      controllerAs: "uploaderCtrl",
      controller: ["$state", SampleMetadataUploaderController]
    })
    .state('sampleId', {
      url: "/sampleId",
      templateUrl: "sampleId.tmpl.html"
    });
  $urlRouterProvider.otherwise('/start');
};
