(function (ng, app) {
  'use strict';
  /**
   * Configure the route parameters
   * $routeProvider
   */
  app.
    config(['$routeProvider', function ($routeProvider) {
      'use strict';
      $routeProvider.when(
        '/projects/:projectId', {
          templateUrl: '/partials/project.html',
          controller: function ($scope, resourceService, initData) {
            $scope.links = resourceService.formatResourceLinks(initData.resource.links);
            $scope.project = initData.resource;
            $scope.users = resourceService.formatRelatedResource(initData.relatedResources.users);
          },
          resolve: {
            initData: function ($route, Restangular) {
              var id = $route.current.params.projectId;
              return Restangular.one('projects', id).get();
            }
          }
        });
    }])

    .controller('ProjectCtrl', ['$scope', 'ajaxService', function ($scope, ajaxService) {
      'use strict';

      $scope.removeUser = function (url) {
        ajaxService.deleteItem(url).then(
          // Success
          function () {
            // TODO: (JOSH - 2013-05-21) Need to refresh the users or remove the deleted one.
            // TODO: (JOSH - 2013-05-21) Show success notification with ability to undo.
          },
          // Error
          function () {

          }
        );
      };

      // Initialize
      $scope.$emit('setWindowTitle', 'Project Page');
    }]);
})(angular, NGS);