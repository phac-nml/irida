(function(angular, page) {
  /**
   * Controller for the updates section.  Requests the updates markdown file.
   * This file is configures in the configuration.properties file.  Defaults to: /resources/updates.md
   * @param $http
   * @constructor
   */
  function UpdateController () {
    var vm = this;
    vm.content = page.updates;
  }

  angular.module('irida.dashboard', ['hc.marked'])
    .controller('updateController', [UpdateController])
  ;
})(window.angular, window.PAGE);