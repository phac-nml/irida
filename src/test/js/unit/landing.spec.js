/**
 * User: josh
 * Date: 2013-05-13
 * Time: 11:02 AM
 */
describe('LandingCtrl', function () {
  var LandingCtrl, $location, $scope, $httpBackend;

  beforeEach(module('NGS'));

  beforeEach(inject(function ($controller, _$location_, $rootScope, $injector) {
    $location = _$location_;
    $scope = $rootScope.$new();
    $httpBackend = $injector.get('$httpBackend');
    $httpBackend.when('GET', url).respond(response);
    LandingCtrl = $controller('LandingCtrl', { $scope: $scope });
  }));

  afterEach(function () {
    'use strict';
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('should have a valid controller', inject(function () {
    expect(LandingCtrl).toBeTruthy();
  }));
});