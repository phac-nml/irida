/*global angular */

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2013-05-08
 * Time: 2:33 PM
 */

angular.module('irida.directives', [])
  .directive('loginModal', function (ajaxService, authService) {
    'use strict';
    return {
      restrict: 'C',
      link: function (scope, el) {
        el.foundation('reveal', {
          closeOnBackgroundClick: false
        });

        scope.$on('event:auth-loginRequired', function () {
          el.foundation('reveal', 'open');
        });
        scope.$on('event:auth-loginConfirmed', function () {
          el.foundation('reveal', 'close');
        });

        scope.login = function () {
          ajaxService.post('/login', {username: scope.username, password: scope.password}).then(
            function () {
              authService.loginConfirmed();
            },
            function () {
              // TODO: Show a message stating that the login credentials are incorrect.
              scope.showError = true;
            }
          );
        };
      }
    };
  });