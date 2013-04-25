describe('IRIDA e2e tests', function() {
	'use strict';

	describe('Landing', function() {
		beforeEach(function() {
			browser().navigateTo('/');
		});

		it('Should have the proper page title', function() {
			expect(element('.name h1 a').text()).toBe("IRIDA");
		});

		it('Should be able to navigate to the projects page by clicking the link.', function() {
			element('a#projectsLink', 'Click on projects link').click();
			expect(browser().window().href()).toBe("http://localhost:9000/projects");
		});
	});

	describe('Projects', function() {
		beforeEach(function() {
			browser().navigateTo('/projects');
		});

		it('Should have the proper url', function () {
		    expect(browser().window().href()).toBe("http://localhost:9000/projects");
		});

		it('Should not be allowed to create a project with no name', function () {
			element('button[data-reveal-id="newProjectModal"]').click();
			// element('a[type="submit"]').query(function () {

			// });
		});

		it('Should be able to create a new project', function() {
			element('button[data-reveal-id="newProjectModal"]').click();
			expect(element('.modal-header h1').text()).toBe('Create New Project');
			input('newProject.name').enter('Test Project');
			element('a[type="submit"]').click();
		});
	});

});