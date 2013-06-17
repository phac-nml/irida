/* =========================================================================
 AUTHOR:   Josh Adam <josh.adam@phac-aspc.gc.ca>
 DATE:     4 June, 2013
 COMMENTS: Runs all the angular scripts for the project.
 LICENSE:  MIT
 ========================================================================= */
var NGS = angular.module( 'NGS', [
    'ngCookies',
    'http-auth-interceptor',
    'ngResource',
    'ui.state',
    'ngs-section',
    'ngDragDrop'
] );

/**
 * Allows for the use of URLs without the !# in it
 */
NGS.config( [ '$locationProvider',
  function ( $locationProvider ) {
    'use strict';
    $locationProvider.html5Mode( true );
  }
] );

NGS.run( [ '$cookieStore', '$http',
  function ( $cookieStore, $http ) {
    'use strict';
    var cookie = $cookieStore.get( 'authdata' );
    if ( cookie ) {
      $http.defaults.headers.common.Authorization = 'Basic ' + $cookieStore.get( 'authdata' );
    }
  }
] );