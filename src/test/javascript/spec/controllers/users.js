/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-04-05
 * Time:   8:46 AM
 */

describe('Controller: newUserModalCtrl', function () {
  'use strict';
  beforeEach(module('sraApp'));

  var testUser = {
    username: 'testUser',
      password: 'pASS1234',
      email: 'test@test.ca',
      phoneNumber: '204-123-4567',
      firstName: 'Test',
      lastName: 'User'
  };

  var UserModalCtrl,
    scope;

  beforeEach(inject(function ($controller) {
    scope = {};

    UserModalCtrl = $controller('newUserModalCtrl', {
      $scope: scope
    });
  }));

  it('should pass this test', function () {
    expect(1).toBe(1);
  });
});