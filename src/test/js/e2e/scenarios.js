describe('IRIDA e2e tests', function() {
	beforeEach(function() {
		browser().navigateTo('/');
	});
	it("Should have the title on the page", function () {
		expect(element('.name h1 a').text()).toBe("IRIDA");
	});

	it('Should be able to navigate to the projects page by clicking the link.', function() {
		element('#projectsLink', 'Click on projects link').click();
		// expect(browser().location().url()).toBe("/projects");
	});

});