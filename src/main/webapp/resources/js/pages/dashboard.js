(function(angular, page) {

  /**
   * Controller for the updates section.
   * @constructor
   */
  function UpdateController () {
    var vm = this;
    vm.content = page.updates;
  }

  angular.module('irida.dashboard', ['hc.marked', 'irida.events'])
    .controller('updateController', [UpdateController])
  ;
})(window.angular, window.PAGE);