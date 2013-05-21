/*global angular */

/**
 * User: josh
 * Date: 2013-05-08
 * Time: 2:33 PM
 */

angular.module('NGS')
/**
 * Injected:
 * @param ajaxService Handles all ajax related calls
 * @param authService Responsible for all http-authentication checking
 */
  .directive('loginModal', function (loginService, authService) {
    'use strict';
    return {
      restrict: 'C',
      link: function (scope, el) {
        scope.lm = {};

        el.foundation('reveal', {
          closeOnBackgroundClick: false
        });

        // Listening for $rootScope authentication broadcast events.
        // Closes and opens the login model
        scope.$on('event:auth-loginRequired', function () {
          el.foundation('reveal', 'open');
        });
        scope.$on('event:auth-loginConfirmed', function () {
          el.foundation('reveal', 'close');
        });

        scope.login = function () {

          if (scope.loginForm.$valid) {
            loginService.setHeader(scope.lm.username, scope.lm.password, function () {
              authService.loginConfirmed();
            });
          }
        };
      }
    };
  });