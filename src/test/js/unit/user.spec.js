/**
 * User: josh
 * Date: 2013-05-13
 * Time: 11:42 AM
 */

describe('UserCtrl', function () {
  var response = {
    "resource": {
      "links": [
        {
          "rel": "user/projects",
          "href": "http://localhost:8080/users/user2/projects"
        },
        {
          "rel": "self",
          "href": "http://localhost:8080/users/user2"
        }
      ],
      "username": "user2",
      "email": "user2@nowhere.com",
      "firstName": "User",
      "lastName": "Number2",
      "phoneNumber": "204-123-4567",
      "dateCreated": 1368461816018
    }
  };

  var UserCtrl, $location, $scope, $httpBackend;
  var url = 'http://localhost:8080/users/user2';

  beforeEach(module('irida'));

  beforeEach(inject(function ($controller, _$location_, $rootScope, $injector, ajaxService) {
    $location = _$location_;
    $scope = $rootScope.$new();
    $httpBackend = $injector.get('$httpBackend');
    UserCtrl = $controller('UserCtrl', { $rootScope: $rootScope, $scope: $scope, $location: $location, ajaxService: ajaxService });
    setUpController();
  }));

  afterEach(function () {
    'use strict';
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('should have a valid controller', inject(function () {
    expect(UserCtrl).toBeTruthy();
  }));

  it('should be able to delete a user user', function () {
    $httpBackend.expectDELETE(url).respond(200, '');
    $scope.deleteUser();
    $httpBackend.flush();
  });

  it('should notify the client when the user is deleted', function () {
    $httpBackend.expectDELETE(url).respond(200, '');
    $scope.deleteUser();
    $httpBackend.flush();
    expect($scope.notifier.message).toBe('Deleted user2');
    expect($scope.notifier.icon).toBe('trash');
  });

  function setUpController() {
    $scope.links = {};
    $scope.notifier = {};
    $scope.user = response.resource;
    $scope.links.self = response.resource.links[1].href;
  }
});