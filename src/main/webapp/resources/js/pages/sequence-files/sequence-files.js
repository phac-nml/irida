(function(ng, page) {
  "use strict";

  function FastQCController(lightbox) {
    var vm = this;
    vm.images = page.images;

    vm.openLightbox = function(index) {
      lightbox.openModal(vm.images, index);
    };
  }

  const sequenceFilesModule = ng
    .module("irida.sequence-files", ["bootstrapLightbox"])
    .config(function(LightboxProvider) {
      LightboxProvider.templateUrl = "lightbox.html";
    })
    .controller("FastQCController", ["Lightbox", FastQCController]).name;

  angular.module("irida").requires.push(sequenceFilesModule);
})(window.angular, window.PAGE);
