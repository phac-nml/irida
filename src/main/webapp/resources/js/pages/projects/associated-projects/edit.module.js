import angular from "angular";
import debounce from "lodash/debounce";
import "../../../vendor/plugins/angular/angular-bootstrap-switch";
import { showNotification } from "../../../modules/notifications";

const editApp = angular
  .module("associated.edit", ["frapontillo.bootstrap-switch"])
  .service("AssociatedService", [
    "$http",
    "$window",
    function($http, $window) {
      const self = this;
      const page = $window.PAGE;

      self.getAssociated = function(params) {
        return $http
          .get(page.urls.local.get, {
            params: params
          })
          .then(result => result.data);
      };

      self.getRemoteAssociated = function(apiId) {
        return $http
          .get(page.urls.remote.get + apiId + "/available")
          .then(result => result.data);
      };

      self.removeAssociatedStatus = function(params) {
        return $http({
          method: "POST",
          url: page.urls.local.remove,
          data: $.param(params),
          headers: { "Content-Type": "application/x-www-form-urlencoded" }
        }).then(result => result.data);
      };

      self.addAssociatedProject = function(params) {
        console.log(params);
        return $http({
          method: "POST",
          url: page.urls.local.add,
          data: $.param(params),
          headers: { "Content-Type": "application/x-www-form-urlencoded" }
        }).then(result => result.data);
      };

      self.addRemoteAssociatedProject = function(params) {
        return $http({
          method: "POST",
          url: page.urls.remote.add,
          data: $.param(params),
          headers: { "Content-Type": "application/x-www-form-urlencoded" }
        }).then(result => result.data);
      };

      self.removeRemoteAssociatedStatus = function(params) {
        return $http({
          method: "POST",
          url: page.urls.remote.remove,
          data: $.param(params),
          headers: { "Content-Type": "application/x-www-form-urlencoded" }
        }).then(result => result.data);
      };

      return self;
    }
  ])
  .controller("AssociatedTableCtrl", [
    "$scope",
    "AssociatedService",
    function($scope, AssociatedService) {
      const $ctrl = this;
      // paging: handle all things to do with the paging of the
      //  - page = the current page being displayed.
      //  - totalAssociated = the total number of associated projects for this project
      //  - totalPages = the total number of pages (totalAssociated / num per page)
      $ctrl.paging = {
        page: 1,
        totalAssociated: 1,
        totalPages: 1
      };

      // defaultCriteria: these are the defaults for creating the table
      const defaultCriteria = {
        page: 0,
        sortDir: "asc",
        sortedBy: "id",
        count: 10
      };

      // filter: used to control the filter section of the page.
      //  - isCollapsed = whether the filter is open on the screen (initially closed)
      //  - states = populates the states select.
      $ctrl.filter = {
        isCollapsed: true
      };

      // filterCriteria: this is passed to the server on each page request.
      //  - initially a copy of the defaultCriteria
      $ctrl.filterCriteria = Object.assign({}, defaultCriteria);

      // When the ajax call is complete the table is recreated.
      $ctrl.fetchResult = function() {
        $ctrl.filterPromise = AssociatedService.getAssociated(
          $ctrl.filterCriteria
        );

        return $ctrl.filterPromise.then(
          data => {
            if (data.hasOwnProperty("associated")) {
              $ctrl.associated = data.associated;
              $ctrl.paging.totalAssociated = data.totalAssociated;
              $ctrl.paging.totalPages = data.totalPages;
            } else {
              // Normally will only hit on bad credentials.
              window.location = window.location;
            }
          },
          function() {
            $ctrl.associated = [];
          }
        );
      };

      // filterResult
      //  - Called when filtering data.
      $ctrl.filterResult = function() {
        $ctrl.filterCriteria.page = 0;
        $ctrl.fetchResult().then(function() {
          $ctrl.paging.page = 1;
        });
      };

      // This watch occurs when a different page is select.  Triggers a server call with the new page.
      $scope.$watch(
        () => $ctrl.paging.page,
        () => {
          $ctrl.filterCriteria.page = $ctrl.paging.page - 1;
          $ctrl.fetchResult();
        }
      );

      // onSort
      $ctrl.onSort = function(sortedBy, sortDir) {
        $ctrl.filterCriteria.sortedBy = sortedBy;
        $ctrl.filterCriteria.sortDir = sortDir;
        $ctrl.filterCriteria.page = 1;
        $ctrl.fetchResult().then(function() {
          $ctrl.filterCriteria.page = 0;
        });
      };

      // This watch occurs when the name is filtered.
      //  'debounce' whats for the given time before calling.  This allows the user to enter
      //  multiple characters without triggering for each one.
      $scope.$watch(
        () => $ctrl.filterCriteria.name,
        debounce((n, o) => {
          if (n !== o) {
            $ctrl.fetchResult().then(() => {
              $ctrl.paging.page = 1;
            });
          }
        }, 500)
      );

      $ctrl.updateAssociated = function(project) {
        const titleElem = angular.element(".navbar-sm li.active span");
        let titleVal = Number(titleElem.html());
        if (project.associated) {
          AssociatedService.addAssociatedProject({
            associatedProjectId: project.id
          }).then(data => {
            if (data.result === "success") {
              // notySuccess(/*[[#{project.associated.added}]]*/ 'Associated project added.');
              // #{project.associated.error}
              showNotification({ text: data.message });
              titleElem.html(++titleVal);
            } else {
              // notyError();
              showNotification({ text: data.message });
              project.associated = "associated";
            }
          });
        } else {
          AssociatedService.removeAssociatedStatus({
            associatedProjectId: project.id
          }).then(data => {
            if (data.result === "success") {
              showNotification({ text: data.message });
              titleElem.html(--titleVal);
            } else {
              showNotification({ text: data.message, type: "error" });
              project.associated = false;
            }
          });
        }
      };
    }
  ]).name;

// Add this to the main app.
const app = angular.module("irida");
app.requires.push(editApp);
