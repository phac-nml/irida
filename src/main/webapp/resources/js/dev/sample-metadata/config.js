import DropzoneController from "./controllers/DropzoneController";

export const states = ($stateProvider, $urlRouterProvider) => {
  $stateProvider
    .state('metadata', {
      url: "/start",
      templateUrl: "upload.tmpl.html",
      controllerAs: "dzCtrl",
      controller: DropzoneController
    });
  $urlRouterProvider.otherwise('/start');
};
