/**
 * User: josh
 * Date: 2013-05-13
 * Time: 11:21 AM
 */

describe('ProjectCtrl', function () {
  var ProjectCtrl, $location, $scope, $httpBackend;
  var url = '/login';

  beforeEach(module('irida'));

  beforeEach(inject(function ($controller, _$location_, $rootScope, $injector) {
    $location = _$location_;
    $scope = $rootScope.$new();
    $httpBackend = $injector.get('$httpBackend');
    ProjectCtrl = $controller('ProjectCtrl', { $scope: $scope });
  }));

  afterEach(function () {
    'use strict';
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('should have a valid controller', inject(function () {
    expect(ProjectCtrl).toBeTruthy();
  }));

});