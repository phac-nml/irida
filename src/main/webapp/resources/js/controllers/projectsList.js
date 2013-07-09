/*global angular, NGS */
/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    9:51 AM
 * License: MIT
 */

(function (ng, app) {
  'use strict';

  app.controller('ProjectsListCtrl', ['$scope', 'ajaxService', '$location', function ($scope, ajaxService, $location) {

    var active = '';

    $scope.createProject = function () {
      // TODO: (Josh: 2013-06-14) Create modal window to facilitate new project.
    };

    $scope.gotoProject = function (e, url) {
      if(active){
        active.removeClass('active');
      }
      active = ng.element(e.currentTarget);
      active.addClass('active');
      $location.path(url.match(/\/projects\/.*$/)[0]);
    };

  }]);
})(angular, NGS);