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
      template: '<div>\
      <div class="form-group">\
      <div class="btn-group">\
      <label class="btn btn-default" ng-model="eventsCtrl.size" btn-radio="10">10</label>\
      <label class="btn btn-default" ng-model="eventsCtrl.size" btn-radio="20">20</label>\
      <label class="btn btn-default" ng-model="eventsCtrl.size" btn-radio="50">50</label>\
      <label class="btn btn-default" ng-model="eventsCtrl.size" btn-radio="100">100</label>\
      </div></div>\
      <div id="events"></div>\
      </div>',
      scope: {
        url: '@'
      },
      replace: true,
      controllerAs: 'eventsCtrl',
      controller: function ($scope, $element, $attrs) {
        var vm = this,
          elm = $element.find('#events');

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
            elm.html($compile(data)($scope));
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