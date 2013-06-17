/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    1:05 PM
 * License: MIT
 */
( function ( ng, app ) {
  'use strict';
  app.controller( 'ProjectCtrl', [ '$scope', '$rootScope', 'ajaxService', '$location',
    function ( $scope, $rootScope, ajaxService, $location ) {
      $scope.samples = {};
      $scope.list2 = [ ];

      $scope.addFileToSample = function ( evt, ui, url ) {
        console.log( $scope.list2 );
        console.log( url );
        // TODO: (Josh: 2013-06-14) MONDAY: FIX THIS.  NEED TO PASS THE DATA 
        ajaxService.create( url, {
          'sequenceFileId': $scope.list2[ 0 ].identifier
        } ).then( function ( data ) {
          console.log( data );
          $scope.list2 = [ ];
        } );
      };

      $scope.deleteProject = function ( ) {
        ajaxService.deleteItem( $scope.project.links.self ).then( function ( ) {
          $rootScope.$broadcast( 'PROJECT_DELETED', {
            'name': $scope.project.name
          } );
          $location.path( '/' );
        } );
      };
    }
  ] );
} )( angular, NGS );