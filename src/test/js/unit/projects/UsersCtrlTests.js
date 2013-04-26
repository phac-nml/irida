describe('UsersListCtrl', function() {
	var newScope, controller, $httpBackend;

	beforeEach(module('irida.services'));

	beforeEach(inject(function($rootScope, $controller, $injector) {
		newScope = $rootScope.$new();
		controller = $controller('UsersListCtrl', {
			$scope: newScope
		});

		$httpBackend = $injector.get('$httpBackend');
		$httpBackend.when('GET', '/users').respond({
			resources: {
				'links': [{
					'rel': 'first',
					'href': 'http://0.0.0.0:8080/users?page=1&size=20&sortOrder=ASCENDING'
				}, {
					'rel': 'next',
					'href': 'http://0.0.0.0:8080/users?page=2&size=20&sortOrder=ASCENDING'
				}, {
					'rel': 'last',
					'href': 'http://0.0.0.0:8080/users?page=6&size=20&sortOrder=ASCENDING'
				}, {
					'rel': 'self',
					'href': 'http://0.0.0.0:8080/users?page=1&size=20&sortOrder=ASCENDING'
				}],
				'resources': [{
					'links': [{
						'rel': 'self',
						'href': 'http://0.0.0.0:8080/users/tom'
					}],
					'username': 'tom',
					'email': 'tom@nowhere.com',
					'firstName': 'Tom',
					'lastName': 'Matthews',
					'phoneNumber': '1234'
				}, {
					'links': [{
						'rel': 'self',
						'href': 'http://0.0.0.0:8080/users/franklin'
					}],
					'username': 'franklin',
					'email': 'franklin@nowhere.com',
					'firstName': 'Franklin',
					'lastName': 'Bristow',
					'phoneNumber': '2345'
				}, {
					'links': [{
						'rel': 'self',
						'href': 'http://0.0.0.0:8080/users/josh'
					}],
					'username': 'josh',
					'email': 'josh@nowhere.com',
					'firstName': 'Josh',
					'lastName': 'Adam',
					'phoneNumber': '3456'
				}, {
					'links': [{
						'rel': 'self',
						'href': 'http://0.0.0.0:8080/users/matt'
					}],
					'username': 'matt',
					'email': 'matt@nowhere.com',
					'firstName': 'Matthew',
					'lastName': 'Stuart-Edwards',
					'phoneNumber': '4567'
				}, {
					'links': [{
						'rel': 'self',
						'href': 'http://0.0.0.0:8080/users/aaron'
					}],
					'username': 'aaron',
					'email': 'aaron@nowhere.com',
					'firstName': 'Aaron',
					'lastName': 'Petkau',
					'phoneNumber': '5678'
				}]
			}
		});
	}));
	afterEach(function() {
		'use strict';
		$httpBackend.verifyNoOutstandingExpectation();
		$httpBackend.verifyNoOutstandingRequest();
	});

	it('Should have an empty users array', function() {
		expect(newScope.users.length).toBe(0);
	});

  it('Should have a users url', function() {
    expect(newScope.usersUrl).toContain('/users?');
  });

  describe('Calling server for users data', function() {
    
  });
});