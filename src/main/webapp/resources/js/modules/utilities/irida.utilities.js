(function ($) {
    "use strict";
    function ngFocus() {
        var FOCUS_CLASS = "ng-focused";
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attrs, ctrl) {
                ctrl.$focused = false;
                element.bind('focus', function () {
                    element.addClass(FOCUS_CLASS);
                    scope.$apply(function () {
                        ctrl.$focused = true;
                    });
                }).bind('blur', function () {
                    element.removeClass(FOCUS_CLASS);
                    scope.$apply(function () {
                        ctrl.$focused = false;
                    });
                });
            }
        };
    }

    function countdownFilter () {
        return function(timeleft) {
            if($.isNumeric(timeleft)){
                return moment.duration(timeleft, "milliseconds").humanize();
            }
        };
    }

    function DownloadIFrame () {
        return {
            restrict: "E",
            template: '<iframe style="display:none;" ng-src="' + TL.BASE_URL + '{{relativeUrl}}' + '"></iframe>'
        }
    }

    angular
        .module('irida.utilities', [])
        .directive('ngFocus', ngFocus)
        .filter('countdown', countdownFilter)
    ;
})(jQuery);