/**
 * Created with JetBrains WebStorm.
 * User: josh
 * Date: 2013-05-07
 * Time: 8:38 AM
 * To change this template use File | Settings | File Templates.
 */

var response = {
  resources: {
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
var url = '/projects';

describe('ProjectsListCtrl', function () {
  describe('isCurrentUrl', function () {
    var ProjectsListCtrl, $location, $scope, $httpBackend;

    beforeEach(module('irida'));

    beforeEach(inject(function ($controller, _$location_, $rootScope, $injector, ajaxService) {
      $location = _$location_;
      $scope = $rootScope.$new();
      $httpBackend = $injector.get('$httpBackend');
      $httpBackend.when('GET', url).respond(response);
      ProjectsListCtrl = $controller('ProjectsListCtrl', {$location: $location, $scope: $scope, ajaxService: ajaxService});
    }));

    afterEach(function () {
      'use strict';
      $httpBackend.verifyNoOutstandingExpectation();
      $httpBackend.verifyNoOutstandingRequest();
    });

    it('Should pass a dumy test', inject(function () {
      expect(ProjectsListCtrl).toBeTruthy();
    }));

    describe('Should set up the page', function () {
      beforeEach(function () {
        $httpBackend.expectGET(url);
      });

      it('Should make a server call for current users', inject(function () {
        $scope.loadProjects(url);
      }));

      it('Should get a list of projects', inject(function () {
        $scope.loadProjects(url);
        waits(1000);
        runs(function() {
          console.log($scope.projects);
          expect($scope.projects.length).toBe(1);
        });

      }));

      afterEach(function () {
        $httpBackend.flush();
      });
    });
  });
});