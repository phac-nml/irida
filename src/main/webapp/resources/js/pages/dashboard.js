(function(angular, page) {
  function EventsService ($http) {
    function getEvents (size) {
      size = typeof size === 'undefined' ? 10 : size;
      return $http.get(page.urls.events, {
        params: { size: size },
        headers: {
          Accept: 'text/html'
        }
      });
    }

    return {
      getEvents: getEvents
    };
  }

  /**
   * Controller for the updates section.
   * @constructor
   */
  function UpdateController () {
    var vm = this;
    vm.content = page.updates;
  }

  function EventsController (eventsSvc, $scope, $compile) {
    var vm = this,
      eventsDiv = angular.element('#events');

    vm.size = 10;

    $scope.$watch(function() {
      return vm.size;
    }, function(n, o) {
      if(o !== n){
        getEvents();
      }
    });

    function getEvents () {
      eventsSvc.getEvents(vm.size).then(function (data) {
        var el = $compile(data.data)($scope);
        eventsDiv.html(el);
      });
    }

    getEvents();
  }

  angular.module('irida.dashboard', ['hc.marked'])
    .service('EventsService', ['$http', EventsService])
    .controller('updateController', [UpdateController])
    .controller('EventsController', ['EventsService', '$scope', '$compile', EventsController])
  ;
})(window.angular, window.PAGE);