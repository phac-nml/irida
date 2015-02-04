(function () {
  "use strict";

  /**
   * Services to call API for SequenceFile
   * @constructor
   */
  function FileService() {
    var svc = this;

    /**
     * Creates a new iFrame to download a file into.
     * @param id Id for the sequenceFile to download
     */
    svc.download = function (id) {
      var iframe = document.createElement("iframe");
      iframe.src = URLS.download + id;
      iframe.style.display = "none";
      document.body.appendChild(iframe);
    };
  }

  /**
   * Controller for the modal to confirm removing a sequenceFile
   * @param $modalInstance Handle on the current modal
   * @param label Name of the file to remove from the Sample.
   * @constructor
   */
  function FileDeletionController($modalInstance, id, label) {
    var vm = this;
    vm.fileId = id;
    vm.label = label;

    vm.deleteFile = function () {
      $modalInstance.close();
    };

    vm.cancel = function () {
      $modalInstance.dismiss();
    };
  }

  /**
   * Controller for the Buttons in the list of sequence files.
   * @param fileService Service for API calls for sequenceFiles
   * @param $modal Angular modal
   * @constructor
   */
  function FileController(fileService, $modal) {
    var vm = this;

    /**
     * Click handler for the download button for a sequenceFile
     * @param id Id for the sequenceFile to download
     */
    vm.download = function (id) {
      fileService.download(id);
    };

    /**
     * Click handler for the delete button for a sequenceFile
     *  Displays a confirmation modal
     * @param id Id for the sequenceFile to delete
     * @param label Name of the sequenceFile to delete
     */
    vm.deleteFile = function(id, label){
      $modal.open({
        templateUrl: '/confirm.html',
        controller: 'FileDeletionController as deleteCtrl',
        resolve: {
          id: function () {
              return id;
          },
          label: function() {
            return label;
          }
        }
      });
    };
  }

  angular.module('irida.sample.files', [])
    .controller('FileController', ['FileService', '$modal', FileController])
    .controller('FileDeletionController', ['$modalInstance', 'id', 'label', FileDeletionController])
    .service('FileService', [FileService])
  ;
})();