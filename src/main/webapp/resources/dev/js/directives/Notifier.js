/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-04-30
 * Time:   8:19 AM
 */

angular.module('irida')
  .directive('notifier', function () {
    'use strict';
    return {
      restrict: 'E',
      scope: {
        message: '@attrMessage',
        icon: '@attrIcon',
        link: '@attrLink'
      },
      replace: true,
      controller: function ($scope, $attrs, $element, $timeout) {
        var time, timer;
        var TIMEOUT = 10;
        $scope.hidden = true;

        // TODO: add link delete stuff here.

        $scope.$on('notify', function () {
          if (time < TIMEOUT) {
            $timeout.cancel(timer);
          }
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
      template: '<div style="display: none" class="ng-cloak notifier" data-ng-hide="hidden" data-ng-animate="\'notifier\'"><span class="left"><i class="icon-{{icon}}"></i> {{message}}</span><a class="right" ng-href="{{link}}">undo</a></div>',
      link: function () {
      }
    };
  });