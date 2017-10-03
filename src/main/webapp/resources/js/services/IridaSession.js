import angular from "angular";
import moment from "moment";

function countdownFilter() {
  return function(timeleft) {
    if ($.isNumeric(timeleft)) {
      return moment.duration(timeleft, "milliseconds").humanize();
    }
  };
}

function runSessionFactory(session) {
  session.start();
}

function SessionFactory($timeout, $interval, $window, $uibModal) {
  const modalWait = 300000; // 5 minutes
  const initialWait = window.TL.SESSION_LENGTH * 1000 - modalWait + 500; // Give a little overlap
  let timeout;
  let restartTimeout;
  let opened = false;

  function _restart() {
    if (!opened) {
      opened = true;
      $uibModal.open({
        templateUrl: "/session-modal.html",
        controller: [
          "$scope",
          "$http",
          "$window",
          "$uibModalInstance",
          function($scope, $http, $window, $uibModalInstance) {
            $scope.poke = function() {
              $interval.cancel(countdown);
              $timeout.cancel(restartTimeout);
              $timeout.cancel(timeout);
              $http.head($window.location.href).then(function() {
                start();
                opened = false;
                $uibModalInstance.close();
              });
            };

            $scope.timeleft = modalWait;
            const countdown = $interval(function() {
              $scope.timeleft = $scope.timeleft - 1000;
            }, 1000);

            restartTimeout = $timeout(function() {
              $window.location.reload();
            }, modalWait);
          }
        ]
      });
    }
  }

  function start() {
    timeout = $timeout(function SessionTimer() {
      _restart();
    }, initialWait);
  }

  function reset() {
    $timeout.cancel(timeout);
    start();
  }

  return {
    start: start,
    reset: reset
  };
}

function HttpInterceptor($injector) {
  return {
    response: function(response) {
      const session = $injector.get("SessionFactory");
      session.reset();
      return response;
    }
  };
}

export const IridaSession = angular
  .module("irida.session", [])
  .run(["SessionFactory", runSessionFactory])
  .factory("SessionFactory", [
    "$timeout",
    "$interval",
    "$window",
    "$uibModal",
    SessionFactory
  ])
  .factory("HttpInterceptor", ["$injector", HttpInterceptor])
  .filter("countdown", countdownFilter).name;
