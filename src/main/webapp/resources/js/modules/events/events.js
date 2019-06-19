/**
 * @file Responsible for loading and setting a table of events..
 * Used on both the Dashboard and Admin > Events pages.
 */

import angular from "angular";
import "../../../sass/modules/events.scss";

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
  function getEvents(url, size = 10) {
    return $http
      .get(url, {
        params: { size },
        headers: {
          Accept: "text/html"
        }
      })
      .then(data => data.data);
  }

  return {
    getEvents
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
      url: "@"
    },
    replace: true,
    controllerAs: "eventsCtrl",
    controller: [
      "$scope",
      "$element",
      function($scope, $element) {
        const vm = this;

        vm.size = 10;
        $scope.$watch(
          function() {
            return vm.size;
          },
          function(n, o) {
            if (n !== o) {
              getEvents();
            }
          }
        );

        function getEvents() {
          svc.getEvents($scope.url, vm.size).then(function(data) {
            $element.html($compile(data)($scope));
            $('[data-toggle="tooltip"]').tooltip();
          });
        }

        getEvents();
      }
    ]
  };
}

export const EventsModule = angular
  .module("irida.events", [])
  .service("EventsService", ["$http", EventsService])
  .directive("events", ["EventsService", "$compile", events]).name;
