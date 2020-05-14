import angular from "angular";
import "marked";
import hcMarked from "angular-marked";
import "./../../../sass/modules/announcements.scss";

/**
 * Service to get announcements DOM from server
 * @param $http
 * @returns {{getAnnouncements: getAnnouncements}}
 * @constructor
 */
function AnnouncementsService($http) {
  function getAnnouncements(url) {
    return $http
      .get(url, {
        headers: {
          Accept: "text/html"
        }
      })
      .then(function(data) {
        return data.data;
      });
  }

  function markAnnouncementRead(url) {
    return $http
      .post(url, {
        headers: {
          Accept: "text/html"
        }
      })
      .then(function(data) {
        return data.data;
      });
  }

  return {
    getAnnouncements: getAnnouncements,
    markAnnouncementRead: markAnnouncementRead
  };
}

/**
 * Announcements directive to replace DOM with list of announcements from server
 * @param svc
 * @param $compile
 * @returns {{template: string, scope: {url: string}, replace: boolean, controllerAs: string, controller: controller}}
 */
function announcements(svc, $compile) {
  return {
    template: "<div></div>",
    scope: {
      url: "@"
    },
    replace: true,
    controllerAs: "announcementCtrl",
    controller: [
      "$scope",
      "$element",
      function($scope, $element) {
        function getAnnouncements() {
          svc.getAnnouncements($scope.url).then(function(data) {
            $element.html($compile(data)($scope));
          });
        }

        getAnnouncements();
      }
    ]
  };
}

export const DashboardAnnouncementsModule = angular
  .module("irida.announcements", [hcMarked])
  .service("AnnouncementsService", ["$http", AnnouncementsService])
  .directive("announcements", [
    "AnnouncementsService",
    "$compile",
    announcements
  ])
  .controller("AnnouncementItemCtrl", [
    "$window",
    "$scope",
    "AnnouncementsService",
    function($window, $scope, AnnouncementsService) {
      $scope.markRead = function markRead(aID, event) {
        var url = window.PAGE.urls.link + "/read/" + aID;
        AnnouncementsService.markAnnouncementRead(url);

        var target = angular
          .element(event.target)
          .parentsUntil(".list-group", ".list-group-item"); //get the list item to hide it
        var listElement = target.parent(); //the list containing all the items
        target.hide(400, function() {
          target.remove();
        });

        var numItems = $("ul#announcement-list").children().length;

        if (numItems <= 1) {
          $("#no-new-announcements").show(500);
          listElement.remove();
        }
      };
    }
  ]).name;
