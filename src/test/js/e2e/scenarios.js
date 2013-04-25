describe('IRIDA e2e tests', function() {
	beforeEach(function() {
		browser().navigateTo('/');
	});
	it("Should have the title on the page", function () {
		expect(element('.name h1 a').text()).toBe("IRIDA");
	});

	it('Should be able to navigate to the projects page by clicking the link.', function() {
		element('a#projectsLink', 'Click on projects link').click();
		expect(browser().window().href()).toBe("http://localhost:9000/projects");
	});

});