describe("ProjectsCtrl", function () {
  var newScope, controller, $httpBackend;

  beforeEach(module('irida'));
  beforeEach(inject(function ($rootScope, $controller, $injector) {
    newScope = $rootScope.$new();
    controller = $controller("ProjectsListCtrl", {$scope: newScope});

    $httpBackend = $injector.get('$httpBackend');
    $httpBackend.when('GET', '/projects').respond({projectResources: {projects: [
      {firstName: 'josh'}
    ]}});
  }));

  afterEach(function () {
    "use strict";
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it("Should have an empty projects array", function () {
    "use strict";
    expect(newScope.projects.length).toBe(0);
  });
//
//  it("Should make a server call", function () {
//    runs(function () {
//      $httpBackend.expectGET('/users');
//      newScope.loadProjects('/users');
//      waits(1000);
//      runs(function () {
//        expect(newScope.users.length).toBe(1);
//      });
//      $httpBackend.flush();
//    });
//  });
});