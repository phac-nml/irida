/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-04-05
 * Time:   8:46 AM
 */

describe('Users Controller', function () {
  'use strict';
  beforeEach(module('fwsApp.controllers'));

  beforeEach(function () {
    this.addMatchers({
      toEqualData: function (expected) {
        return angular.equals(this.actual, expected);
      }
    });
  });

  var testUser =
    [
      {
        username   : 'testUser',
        password   : 'pASS1234',
        email      : 'test@test.ca',
        phoneNumber: '204-123-4567',
        firstName  : 'Test',
        lastName   : 'User'
      },
      {
        username   : 'testUser2',
        password   : 'pASS1235',
        email      : 'test2@test.ca',
        phoneNumber: '204-123-4567',
        firstName  : 'Test2',
        lastName   : 'User2'
      }
    ];

  describe('usersListCtrl', function () {
    var scope, ctrl, $httpBackend;

    beforeEach(inject(function (_$httpBackend_, $rootScope, $controller) {

      $httpBackend = _$httpBackend_;
      $httpBackend.expectGET('/users').respond(testUser);

      scope = $rootScope.$new();

      ctrl = $controller(usersListCtrl, {$scope: scope});
    }));

    it('should pass this test', function () {
      expect(1).toBe(1);
    });

    it('should have users', function () {
      expect(scope.users).toEqual(1);
    });
  });
});