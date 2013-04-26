describe('ProjectsListCtrl', function() {
  var newScope, controller, $httpBackend;

  beforeEach(module('irida.services'));

  beforeEach(inject(function($rootScope, $controller, $injector) {
    newScope = $rootScope.$new();
    controller = $controller('ProjectsListCtrl', {
      $scope: newScope
    });

    $httpBackend = $injector.get('$httpBackend');
    $httpBackend.when('GET', '/projects').respond({
      resources: {
        'links': [{
          'rel': 'first',
          'href': 'http://0.0.0.0:8080/projects?page=1&size=20&sortOrder=DESCENDING'
        }, {
          'rel': 'next',
          'href': 'http://0.0.0.0:8080/projects?page=2&size=20&sortOrder=DESCENDING'
        }, {
          'rel': 'last',
          'href': 'http://0.0.0.0:8080/projects?page=7&size=20&sortOrder=DESCENDING'
        }, {
          'rel': 'self',
          'href': 'http://0.0.0.0:8080/projects?page=1&size=20&sortOrder=DESCENDING'
        }],
        resources: [{
          name: 'E. coli',
          links: [{
            rel: 'self',
            href: 'http://127.0.0.1:8080/projects/4aee0abd-d6f1-484b-992d-8bf9bd5c7344'
          }]
        }]
      }
    });
  }));
  afterEach(function() {
    'use strict';
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('Should have an empty projects array', function() {
    expect(newScope.projects.length).toBe(0);
  });

  it('Should have a projects url', function() {
    expect(newScope.projectsUrl).toContain('/projects?');
  });

  it('Should make a server call to get the current users', function() {
    runs(function() {
      $httpBackend.expectGET('/projects');
      newScope.loadProjects('/projects');
      waits(1000);
      runs(function() {
        expect(newScope.projects.length).toBe(1);
        expect(newScope.projects[0].name).toBe('E. coli');
      });
      $httpBackend.flush();
    });
  });

  it('Should make a server call and set the page links', function() {
    runs(function() {
      $httpBackend.expectGET('/projects');
      newScope.loadProjects('/projects');
      runs(function() {
        expect(newScope.links.first).toBe('http://0.0.0.0:8080/projects?page=1&size=20&sortOrder=DESCENDING');
      });
      $httpBackend.flush();
    });
  });
});