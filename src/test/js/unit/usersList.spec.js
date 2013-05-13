/**
 * User: josh
 * Date: 2013-05-13
 * Time: 12:24 PM
 */

describe('UsersListCtrl', function () {
  var UsersListCtrl, $location, $scope, $httpBackend;

  beforeEach(module('irida'));

  beforeEach(inject(function ($controller, _$location_, $rootScope, $injector, ajaxService) {
    $location = _$location_;
    $scope = $rootScope.$new();
    $httpBackend = $injector.get('$httpBackend');
    UsersListCtrl = $controller('UsersListCtrl', { $rootScope: $rootScope, $scope: $scope, $location: $location, ajaxService: ajaxService });
  }));

  afterEach(function () {
    'use strict';
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('should have a valid controller', inject(function () {
    expect(UsersListCtrl).toBeTruthy();
  }));
});