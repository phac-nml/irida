/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-04-30
 * Time:   8:19 AM
 */

angular.module('NGS')
  .directive('notifier', function () {
    'use strict';
    return {
      replace: true,
      controller: function ($scope, $attrs, $element, $timeout, ajaxService) {
        $scope.data = {};
        $scope.noty = {
          show: false
        };

        var time, timer;
        var TIMEOUT = 10;

        // TODO: add link delete stuff here.
        $scope.undo = function () {
          $scope.data.callback();
          $timeout.cancel(timer);
          $scope.noty.show = false;
        };

        $scope.$on('NOTIFY', function (event, message) {
          $scope.data = {
            msg: message.msg,
            callback: message.callback
          };

          if (time < TIMEOUT) {
            $timeout.cancel(timer);
          }
          time = TIMEOUT;
          $scope.noty.show = true;
          countDown();
        });

        function countDown() {
          time -= 1;
          if (time > 0) {
            timer = $timeout(countDown, 1000);
          }
          else {
            $timeout.cancel(timer);
            $scope.noty.show = false;
          }
        }
      },
      templateUrl: '/partials/notifier.html'
    };
  });