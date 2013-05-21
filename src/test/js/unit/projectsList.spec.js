/**
 * Created with JetBrains WebStorm.
 * User: josh
 * Date: 2013-05-07
 * Time: 8:38 AM
 * To change this template use File | Settings | File Templates.
 */

var response = {
  resource: {
    'links': [
      {
        'rel': 'first',
        'href': 'http://0.0.0.0:8080/projects?page=1&size=20&sortOrder=DESCENDING'
      },
      {
        'rel': 'next',
        'href': 'http://0.0.0.0:8080/projects?page=2&size=20&sortOrder=DESCENDING'
      },
      {
        'rel': 'last',
        'href': 'http://0.0.0.0:8080/projects?page=7&size=20&sortOrder=DESCENDING'
      },
      {
        'rel': 'self',
        'href': 'http://0.0.0.0:8080/projects?page=1&size=20&sortOrder=DESCENDING'
      }
    ],
    resources: [
      {
        name: 'E. coli',
        links: [
          {
            rel: 'self',
            href: 'http://127.0.0.1:8080/projects/4aee0abd-d6f1-484b-992d-8bf9bd5c7344'
          }
        ]
      }
    ]
  }
};
var url = '/projects?size=21&sortOrder=ASCENDING';

describe('ProjectsListCtrl', function () {
  var ProjectsListCtrl, $location, $scope, $httpBackend;

  beforeEach(module('NGS'));

  beforeEach(inject(function ($controller, _$location_, $rootScope, $injector, ajaxService) {
    $location = _$location_;
    $scope = $rootScope.$new();
    $httpBackend = $injector.get('$httpBackend');
    $httpBackend.when('GET', url).respond(response);
    ProjectsListCtrl = $controller('ProjectsListCtrl', { $rootScope: $rootScope, $scope: $scope, $location: $location, ajaxService: ajaxService});
  }));

  afterEach(function () {
    'use strict';
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('should have a valid controller', inject(function () {
    expect(ProjectsListCtrl).toBeTruthy();
  }));

  it('should fetch the current list of projects', function () {
    'use static';
    $httpBackend.expectGET(url);
    $scope.loadProjects(url);
    $httpBackend.flush();
    expect($scope.projects.length).toBe(1);
  });

});