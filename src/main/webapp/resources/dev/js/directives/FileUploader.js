/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-04-30
 * Time:   12:18 PM
 */

angular.module('irida.directives', [])
  .directive('file', function () {
  "use strict";
  return {
    restrict: 'A',
    scope: {
      files: '='
    },
    link: function (scope, el, attrs) {
      el.bind('change', function (event) {
        var files = event.target.files;
        //          scope.file = file ? file.name : undefined;

        initializeFileUploads(files, this);
        scope.$apply();
      });

      function initializeFileUploads(files, element) {
        scope.uploadedFiles = [];
        angular.forEach(files, function (value, key) {
          uploadFile(value, element);
        });
      }

      function uploadFile(file, element) {
        var uri = "/sequenceFiles";
        var xhr = new XMLHttpRequest();
        var fd = new FormData();

        var el = angular.element(element).parent();
        var outer = angular.element('<div class="progress large-6 success radius"></div>');
        var inner = angular.element('<span class="meter" ><span>' + file.name + '</span></span>');
        var counter = angular.element('<em></em>');
        outer.append(inner);
        outer.append(counter);
        scope.$apply(function () {
          el.append(outer);
        });

        var innerWidth = inner.width();
        xhr.upload.addEventListener('progress', function (e) {
          if (e.lengthComputable) {
            var percentage = e.loaded / e.total;
            var width = Math.round(percentage * innerWidth);
            scope.$apply(function () {
              counter.text(Math.round(percentage * 100) + " %");
              inner.css('width', width)
            });

          }
        }, false);

        xhr.upload.addEventListener('load', function (e) {
          outer.fadeOut(function () {
            this.remove();
          });
        });

        xhr.open("POST", uri, true);
        xhr.onreadystatechange = function () {
          if (xhr.readyState == 4 && xhr.status == 200) {
            // Handle response.
            alert(xhr.responseText); // handle response.

          }
        };
        fd.append('file', file);
        // Initiate a multipart/form-data upload
        xhr.send(fd);
      }
    }
  };
});