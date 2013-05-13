describe('IRIDA e2e tests', function () {
  'use strict';

// TODO: LOOK AT THIS WHEN TESTING AUTHORIZATION
//  it('should fire "event:auth-loginRequired" event in case of 401', function() {
//    var flag = false;
//    var listener = jasmine.createSpy('listener');
//    scope.$on('event:auth-loginRequired', listener);
//    $httpBackend.when('GET', myApi.config.apiRoot + '/user').respond(401, '');
//
//    runs(function() {
//      myApi.get(function(error, success) {
//        // this never gets triggered as 401 are intercepted
//      });
//      setTimeout(function() {
//        flag = true;
//      }, 1000);
//    });
//
//    waitsFor(function() {
//      return flag;
//    }, 'should be completed', 1200);
//
//    runs(function() {
//      expect(listener).toHaveBeenCalled();
//    });
//  });

  /**
   * @Landing Page tests
   */
  describe('Landing', function () {
    beforeEach(function () {
      browser().navigateTo('/');
    });

    it('Should have the proper page title', function () {
      expect(element('.name h1 a').text()).toBe("IRIDA");
    });

    it('Should have the links set up properly', function () {
      expect(element('.top-bar-section li').count()).toBe(3);
      expect(element('.top-bar-section li:nth-child(1)').text()).toEqual('Projects');
      expect(element('.top-bar-section li:nth-child(2)').text()).toEqual('Users');
      expect(element('.top-bar-section li:nth-child(3)').text()).toEqual('Logout');

    });

    it('Should be able to navigate to the projects page by clicking the link.', function () {
      element('.top-bar-section li:nth-child(1) a', 'Click on projects link').click();
      expect(browser().window().href()).toBe("http://localhost:9000/#!/projects");
      expect(element('li.current a').text()).toEqual('All Projects');
    });

    it('Should be able to navigate to the users page by clicking the link.', function () {
      element('.top-bar-section li:nth-child(2) a', 'Click on projects link').click();
      expect(browser().window().href()).toBe("http://localhost:9000/#!/users");
      expect(element('li.current a').text()).toEqual('All Users');
    });
  });

  /**
   * @Projects Page Tests
   */
  describe('Projects', function () {
    beforeEach(function () {
      browser().navigateTo('/#!/projects');
    });

    it('Should have the proper url', function () {
      expect(browser().window().href()).toBe("http://localhost:9000/#!/projects");
    });

    it('Should have 20 projects displayed on the page', function () {
      expect(repeater('.small-block-grid-2 li').count()).toBe(20);
    });

    it('Should not be allowed to create a project with no name', function () {
      element('button[data-reveal-id="newProjectModal"]').click();
      expect(element('a#btnCreateProject').attr('class')).toContain('disabled');
    });

    it('Should not be allowed to create a project if they have deleted the name', function () {
      element('button[data-reveal-id="newProjectModal"]').click();
      expect(element('a#btnCreateProject').attr('class')).toContain('disabled');
      input('newProject.name').enter('fred');
      input('newProject.name').enter('');
      expect(element('a#btnCreateProject').attr('class')).toContain('disabled');

    });

    it('Should be able to create a new project', function () {
      element('button[data-reveal-id="newProjectModal"]').click();
      expect(element('.modal-header h1').text()).toBe('Create New Project');
      input('newProject.name').enter('Test Project');
      element('a#btnCreateProject').click();
    });
  });

});