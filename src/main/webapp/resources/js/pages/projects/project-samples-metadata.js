function uploadMetadataFile() {
  var file = this.files[0];
  if (typeof file === 'undefined') return;

  var self = this;
  var formData = new FormData();
  var xhr = new XMLHttpRequest();

  this.xhr = xhr;
  // this.xhr.upload.addEventListener("progress", function(e) {
  //   if (e.lengthComputable) {
  //     var percentage = Math.round((e.loaded * 100) / e.total);
  //     console.log(percentage);
  //   }
  // }, false);

  formData.append('file', file);
  xhr.open("POST", window.location.href);
  xhr.onload = function(e) {
    console.log(e)
  };
  xhr.send(formData);

}

var fileUploader = document.querySelector("#file-uploader");
var fileUploaderBtn = document.querySelector("#file-uploader-btn");

fileUploaderBtn.addEventListener('click', function() {
  fileUploader.click();
}, false);
fileUploader.addEventListener('change', uploadMetadataFile, false);