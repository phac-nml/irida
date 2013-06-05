/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    2:00 PM
 * License: MIT
 */

angular.module ( 'ngs-section', [] ).directive ( 'ngsSection', function () {
    return {
      restrict: 'A',
      link: function ( scope, elm, atts ) {
        elm.foundation ( 'section' );
      }
    };
  } );