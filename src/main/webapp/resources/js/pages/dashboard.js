(function(angular, page) {
  /**
   * Controller for the updates section.  Requests the updates markdown file.
   * This file is configures in the configuration.properties file.  Defaults to: /resources/updates.md
   * @param $http
   * @constructor
   */
  function UpdateController ($http) {
    var vm = this;
    vm.content = "";

    if (page.urls.updates) {
      $http.get(page.urls.updates)
        .success(function (data) {
          vm.content = data;
        });
    }
  }

  angular.module('irida.dashboard', ['hc.marked'])
    .controller('updateController', ['$http', UpdateController])
  ;
})(window.angular, window.PAGE);