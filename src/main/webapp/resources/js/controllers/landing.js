/**
 * Created by josh on 2013-06-20.
 */
(function (ng, app) {
  'use strict';

  app
    .controller('LandingCtrl', ['$state', function ($state) {
      if ($state.current.name === 'projects') {
        $state.transitionTo('projects.main');
      }
    }]);
})(angular, NGS);