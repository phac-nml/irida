/**
 * User: josh
 * Date: 2013-05-13
 * Time: 11:42 AM
 */
describe('UserCtrl', function () {
  var UserCtrl, $location, $scope, $httpBackend;
  var url = '/login';

  beforeEach(module('irida'));

  beforeEach(inject(function ($controller, _$location_, $rootScope, $injector) {
    $location = _$location_;
    $scope = $rootScope.$new();
    $httpBackend = $injector.get('$httpBackend');
    UserCtrl = $controller('UserCtrl', { $scope: $scope });
  }));

  afterEach(function () {
    'use strict';
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('should have a valid controller', inject(function () {
    expect(UserCtrl).toBeTruthy();
  }));

});