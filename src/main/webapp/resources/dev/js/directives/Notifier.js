/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-04-30
 * Time:   8:19 AM
 */

angular.module('NGS')
  .directive('ngsNotifier', function () {
    'use strict';
    return {
      restrict: 'E',
      scope: {
        message: '@attrMessage',
        icon: '@attrIcon',
        selfLink: '@attrLink'
      },
      replace: true,
      controller: function ($scope, $attrs, $element, $timeout, ajaxService) {
        var time, timer;
        var TIMEOUT = 10;
        $scope.el = $element.find('div');
        $scope.elLeft = $element.find('span.left');
        $scope.elRightWidth = $element.find('a').width();
        $scope.hidden = true;

        $scope.el.bind('blur', function () {
          console.log('blurred');
        });

        // TODO: add link delete stuff here.
        $scope.undo = function (link) {
          ajaxService.deleteItem(link);
          $timeout.cancel(timer);
          $scope.hidden = true;
        };

        $scope.$on('notify', function () {
          if (time < TIMEOUT) {
            $timeout.cancel(timer);
          }
          // Recreate the size of the notification box
          var w = $scope.elLeft.width() + $scope.elRightWidth;
          $scope.el.focus();// todo: not working :(
          time = TIMEOUT;
          $scope.hidden = false;
          countDown();
        });

        function countDown() {
          time -= 1;
          if (time > 0) {
            timer = $timeout(countDown, 1000);
          }
          else {
            $timeout.cancel(timer);
            $scope.hidden = true;
          }
        }
      },
      templateUrl: './partials/notifier.html',
      link: function (scope, el) {
      }
    };
  });