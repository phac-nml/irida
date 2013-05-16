/**
 * User: josh
 * Date: 2013-05-15
 * Time: 1:08 PM
 */
describe("E2E: Testing Routes", function () {
  'use strict';

  beforeEach(function () {
    browser().navigateTo('/login.html');
    input('username').enter('josh');
    input('password').enter('password1');
    element('.button').click();
  });

  it ('should start at the landing page', function () {
    expect(browser().location().path()).toBe("");
  });

  it('should navigate to the projects page', function () {
    browser().navigateTo('/projects');
    expect(browser().location().path()).toBe("/projects");
  });
});