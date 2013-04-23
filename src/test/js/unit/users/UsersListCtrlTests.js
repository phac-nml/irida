describe("UsersListController", function () {
  var newScope, controller, $httpBackend;

  beforeEach(module('irida'));
  beforeEach(inject(function ($rootScope, $controller, $injector) {
    newScope = $rootScope.$new();
    controller = $controller("UsersListCtrl", {$scope: newScope});

    $httpBackend = $injector.get('$httpBackend');
    $httpBackend.when('GET', '/users').respond({userResources: {users: [
      {firstName: 'josh'}
    ]}});
  }));

  afterEach(function () {
    "use strict";
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it("Should have an empty users array", function () {
    "use strict";
    expect(newScope.users.length).toBe(0);
  });

  it("Should have a users url", function () {
    expect(newScope.usersUrl).toContain('/users?');
  });

  it("Should make a server call", function () {
    runs(function () {
      $httpBackend.expectGET('/users');
      newScope.loadUsers('/users');
      waits(1000);
      runs(function () {
        expect(newScope.users.length).toBe(1);
      });
      $httpBackend.flush();
    });
  });
});