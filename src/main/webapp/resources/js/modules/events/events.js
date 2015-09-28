(function (angular) {
  'use strict';
  function EventsService($http) {
    function getEvents(url, size) {
      size = typeof size === 'undefined' ? 10 : size;
      return $http.get(url, {
        params: {size: size},
        headers: {
          Accept: 'text/html'
        }
      }).then(function (data) {
        return data.data;
      });
    }

    return {
      getEvents: getEvents
    };
  }

  function events(svc, $compile) {

    return {
      template: "<div></div>",
      scope: {
        url: '@'
      },
      replace: true,
      controllerAs: 'eventsCtrl',
      controller: function ($scope, $element) {
        var vm = this;

        vm.size = 10;
        $scope.$watch(function () {
          return vm.size;
        }, function (n, o) {
          if(n!==o) {
            getEvents();
          }
        });

        function getEvents() {
          svc.getEvents($scope.url, vm.size).then(function(data) {
            $element.html($compile(data)($scope));
          });
        }

        getEvents();
      }
    };
  }

  angular.module('irida.events', [])
    .service('EventsService', ['$http', EventsService])
    .directive('events', ['EventsService', '$compile', events])
  ;
})(window.angular);