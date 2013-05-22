/**
 * User: josh
 * Date: 2013-05-13
 * Time: 12:24 PM
 */

describe('UsersListCtrl', function () {
  var UsersListCtrl, $location, $scope, $httpBackend;
  var response = {
    'resource': {
      'links': [
        {
          'rel': 'first',
          'href': 'http://localhost:8080/users?page=1&size=20&sortOrder=ASCENDING'
        },
        {
          'rel': 'next',
          'href': 'http://localhost:8080/users?page=2&size=20&sortOrder=ASCENDING'
        },
        {
          'rel': 'last',
          'href': 'http://localhost:8080/users?page=6&size=20&sortOrder=ASCENDING'
        },
        {
          'rel': 'self',
          'href': 'http://localhost:8080/users?page=1&size=20&sortOrder=ASCENDING'
        }
      ],
      'resources': [
        {
          'links': [
            {
              'rel': 'self',
              'href': 'http://localhost:8080/users/tomX'
            }
          ],
          'username': 'tomX',
          'email': 'tom@nowhere.com',
          'firstName': 'Tom',
          'lastName': 'Matthews',
          'phoneNumber': '1234',
          'dateCreated': 1368461815490
        }
      ]
    }
  };

  beforeEach(module('NGS'));

  beforeEach(inject(function ($controller, _$location_, $rootScope, $injector, ajaxService) {
    $location = _$location_;
    $scope = $rootScope.$new();
    $httpBackend = $injector.get('$httpBackend');
    UsersListCtrl = $controller('UsersListCtrl', { $rootScope: $rootScope, $scope: $scope, $location: $location, ajaxService: ajaxService });
    formatLink();
  }));

  afterEach(function () {
    'use strict';
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('should have a valid controller', inject(function () {
    expect(UsersListCtrl).toBeTruthy();
  }));

  it('should load another page of users', function () {
    $httpBackend.expectGET(response.resource.links[0].href).respond(response);
    $scope.loadUsers(response.resource.links[0].href);
    $httpBackend.flush();
  });

  function formatLink() {
    'use strict';
    $scope.links = response.resource.links;
    $scope.users = response.resource.resources;
  }
});