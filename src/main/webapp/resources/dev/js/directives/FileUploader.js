/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-04-30
 * Time:   12:18 PM
 */

angular.module('NGS')
  .directive('file', function () {
    'use strict';
    return {
      restrict: 'A',
      scope: {
        files: '='
      },
      link: function (scope, el) {
        el.bind('change', function (event) {
          var files = event.target.files;

          initializeFileUploads(files, this);
          scope.$apply();
        });

        function initializeFileUploads(files, element) {
          scope.uploadedFiles = [];
          angular.forEach(files, function (value) {
            uploadFile(value, element);
          });
        }

        function uploadFile(file, element) {
          var uri = '/sequenceFiles';
          var xhr = new XMLHttpRequest();
          var fd = new FormData();

          var el = angular.element(element).parent();
          var outer = angular.element('<div class="file-uploader"></div>');
          var inner = angular.element('<span></span>');
          var fileName = angular.element('<div>' + file.name + '</div>');
          var counter = angular.element('<em></em>');
          outer.append(fileName);
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
                counter.text(Math.round(percentage * 100) + ' %');
                inner.css('width', width);
              });

            }
          }, false);

          xhr.upload.addEventListener('load', function () {
            outer.fadeOut(function () {
              this.remove();
            });
          });

          xhr.open('POST', uri, true);
          xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
              // TODO: Handle response.
              console.log('Need to handle these');
            }
          };
          fd.append('file', file);
          xhr.send(fd);
        }
      }
    };
  });