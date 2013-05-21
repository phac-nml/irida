angular.module('irida')

/**
 * Configure the route parameters
 * $routeProvider
 */
  .config(['$routeProvider', function ($routeProvider) {
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
          initData: function ($q, $route, ajaxService) {
            var defer = $q.defer();
            var id = $route.current.params.projectId;
            ajaxService.get('/api/projects/' + id).then(function (data) {
              defer.resolve(data);
            });
            return defer.promise;
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
  }]);