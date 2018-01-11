/**
 * SubNav controls.  This directive should be used on all pages that require sub-navigation.
 * ui-router should be loaded on the page.
 *
 * Expected use:
 * <subnav>
 *  <sub-nav-item state='stateString' text='linkString'></sub-nav-item>
 * </subnav>
 */
(function(angular) {
  angular
    .module("subnav", [])
    .directive("subNav", function() {
      return {
        restrict: "E",
        replace: true,
        transclude: true,
        priority: -1,
        template:
          '<div style="padding-bottom: 10px;"><ul class="nav nav-tabs"><li ng-click="select(link)" ng-class="{active: link.selected}" ng-repeat="link in links"><a id="{{link.state}}" ui-sref="{{link.state}}">{{link.text}}</a></li></ul><div ng-transclude></div></div>',
        controller: [
          "$scope",
          "$location",
          function($scope, $location) {
            $scope.links = [];

            $scope.select = function(link) {
              angular.forEach($scope.links, function(eachLink) {
                eachLink.selected = angular.equals(link, eachLink);
              });
            };

            var path = $location.path();
            this.addLink = function(link) {
              if (path === "" && $scope.links.length === 0) {
                link.selected = true;
                $location.path("/" + link.state);
              } else if (path.indexOf(link.state) > -1) {
                link.selected = true;
              }
              $scope.links.push(link);
            };
          }
        ]
      };
    })
    /**
     * Creates a ui-sref for ui-router for each item
     * @param state - corresponds to the state in the ui-router config
     * @param text - text to display on the link.
     */
    .directive("subNavItem", function() {
      return {
        require: "^subNav",
        restrict: "E",
        replace: true,
        template: "",
        scope: {
          state: "@",
          text: "@"
        },
        link: function(scope, element, attrs, SubNavCtrl) {
          SubNavCtrl.addLink(scope);
        }
      };
    });
})(window.angular);
