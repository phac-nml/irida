/**
 * Created by josh on 2013-06-20.
 */
(function (ng, app) {
  'use strict';

  app
    .controller('LandingCtrl', ['ajaxService', function(ajaxService) {
          ajaxService.get('/api/users/current').then(function(data){
             console.log(data);
              $scope.user =data.resource;
          });
//      if ($state.current.name === 'projects') {
//        $state.transitionTo('projects.main');
//      }
    }]);
})(angular, NGS);
