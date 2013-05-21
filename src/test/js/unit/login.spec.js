/**
 * User: josh
 * Date: 2013-05-13
 * Time: 11:06 AM
 */

describe('LoginCtrl', function () {
  var LoginCtrl, $location, $scope, $httpBackend;
  var url = '/login';

  beforeEach(module('NGS'));

  beforeEach(inject(function ($controller, _$location_, $rootScope, $injector) {
    $location = _$location_;
    $scope = $rootScope.$new();
    $httpBackend = $injector.get('$httpBackend');
    LoginCtrl = $controller('LoginCtrl', { $scope: $scope });
  }));

  afterEach(function () {
    'use strict';
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('should have a valid controller', inject(function () {
    expect(LoginCtrl).toBeTruthy();
  }));

//  it('should contact the server to login', function () {
//    'use strict';
//    $httpBackend.expectPOST('/login?password=pass1234&username=josh').respond(302, '');
//    $scope.username = 'josh';
//    $scope.password = 'pass1234';
//    $scope.login();
//    $httpBackend.flush();
//  });
});