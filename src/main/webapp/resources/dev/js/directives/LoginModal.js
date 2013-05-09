/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2013-05-08
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */

angular.module('irida.directives', [])
  .directive('loginModal', function (ajaxService, authService) {
    "use strict";
    return {
      restrict: 'C',
      link: function (scope, el, attrs) {
        el.foundation('reveal', {
          closeOnBackgroundClick: false
        });

//        el.foundation('reveal', 'open');
        scope.$on('event:auth-loginRequired', function () {
          el.foundation('reveal', 'open');
        });
        scope.$on('event:auth-loginConfirmed', function () {
          el.foundation('reveal', 'close');
        });

        scope.login = function () {
          ajaxService.post('/login', {username: scope.username, password: scope.password}).then(
            function () {
              console.log("success");
              authService.loginConfirmed();
            },
            function () {
              console.log("error")
              // TODO: Show a message stating that the login credentials are incorrect.
              scope.showError = true;
            }
          );
        };
      }
    }
  });
//  .animation('show-animation', function () {
//    return {
//      setup: function (element) {
//        "use strict";
//        element.css({ 'opacity': 0 });
//        element.find('div').css({'top':-200});
//      },
//      start: function (element, done) {
//        "use strict";
//        element.css({ 'opacity': 1 });
//        var child = element.find('div');
//        child.animate({'top': 50}, function () {
//          done();
//        });
//
//      },
//    };
//  })
//  .animation('hide-animation', function () {
//    return {
//      setup: function (element) {
//        element.css({ 'opacity': 1 });
//        element.find('div').css({'top':50});
//      },
//      start: function (element, done) {
//        "use strict";
//        var child = element.find('div');
//        child.animate({
//          'top':-200
//        }, function () {
//          element.css({ 'opacity': 0 });
//          done();
//        });
//      },
//    };
//  });