(function (angular) {
  'use strict';

  /**
   * Service to get events DOM from server.
   * @param $http
   * @returns {{getEvents: getEvents}}
   * @constructor
   */
  function EventsService($http) {
    /**
     *
     * @param url
     * @param size - defaults to 10 if not supplied.
     * @returns {*}
     */
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

  /**
   * Events directive. Replaces DOM on page with the updated events list.
   * @param svc - EventsService
   * @param $compile
   * @returns {{template: string, scope: {url: string}, replace: boolean, controllerAs: string, controller: controller}}
   */
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