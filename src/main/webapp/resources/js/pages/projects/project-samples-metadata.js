
// function uploadMetadataFile() {
//   var file = this.files[0];
//   if (typeof file === 'undefined') return;
//
//   var self = this;
//   var formData = new FormData();
//   var xhr = new XMLHttpRequest();
//
//   this.xhr = xhr;
//   // this.xhr.upload.addEventListener("progress", function(e) {
//   //   if (e.lengthComputable) {
//   //     var percentage = Math.round((e.loaded * 100) / e.total);
//   //     console.log(percentage);
//   //   }
//   // }, false);
//
//   formData.append('file', file);
//   xhr.open("POST", window.location.href);
//   xhr.onload = function(e) {
//     console.log(e)
//   };
//
//   xhr.onreadystatechange = function() {
//     if(xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
//       console.log(xhr.response);
//       createTable(xhr.response);
//     }
//   };
//
//   xhr.send(formData);
//
// }
//
// var fileUploader = document.querySelector("#file-uploader");
// var fileUploaderBtn = document.querySelector("#file-uploader-btn");
//
// fileUploaderBtn.addEventListener('click', function() {
//   fileUploader.click();
// }, false);
// fileUploader.addEventListener('change', uploadMetadataFile, false);

var tableCreator = (function() {
  return function createTable(data) {
    var keys = Object.keys(data[0]);
    var columns = keys.map(function(key) {
      return {title: key, data: key}
    });
    console.log(columns);
    $("#metadata-table").DataTable({
      scrollX: true,
      data: data,
      columns: columns
    });
  };
}());

(function(ng) {
  function FileController(Upload, $window) {
    var vm = this;

    vm.upload = function ($file) {
      Upload.upload({
        url: $window.location.href,
        data: {
          file: $file
        }
      }).then(function(result) {
        tableCreator(result.data);
      });
    };
  }

  ng.module('irida.metadata', ['ngFileUpload'])
    .controller('FileController',['Upload', '$window', FileController]);
}(window.angular));