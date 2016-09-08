import SampleMetadataUploaderController
  from "./controllers/SampleMetadataUploaderController";
import SampleMetadataSampleIdController
  from "./controllers/SampleMetadataSampleIdController";

const URLS = {
  upload: "/upload",
  sampleId: "/sampleId"
};

const uploadState = {
  url: URLS.upload,
  templateUrl: "upload.tmpl.html",
  controllerAs: "uploaderCtrl",
  controller: ["$state", SampleMetadataUploaderController]
};

const sampleIdState = {
  url: URLS.sampleId,
  params: {headers: null},
  templateUrl: "sampleId.tmpl.html",
  controllerAs: "sampleIdCtrl",
  controller: ["$stateParams", SampleMetadataSampleIdController]
};

export const states = ($stateProvider, $urlRouterProvider) => {
  $stateProvider
    .state("upload", uploadState)
    .state("sampleId", sampleIdState);

  // Set the initial view as the upload view
  $urlRouterProvider.otherwise(URLS.upload);
};
