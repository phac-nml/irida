/**
 * Created with JetBrains WebStorm.
 * User: josh
 * Date: 2013-05-07
 * Time: 8:28 AM
 * To change this template use File | Settings | File Templates.
 */

describe('AppCtrl', function () {
  describe('isCurrentUrl', function () {
    var AppCtrl, $location, $scope;

    beforeEach(module('irida'));

    beforeEach( inject( function ($controller, _$location_, $rootScope) {
      $location = _$location_;
      $scope = $rootScope.$new();
      AppCtrl = $controller( 'AppCtrl', { $location : $location, $scope: $scope});
    }));

    it( 'Should pass a dummy test', inject( function () {
      expect(AppCtrl).toBeTruthy();
    }));
  });
});