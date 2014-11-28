var LoginPage = require('./pages/login.page');
var DashboardPage = require('./pages/dashboard.page');

describe('Index', function () {
    'use strict';
    var dashboardPage;

    beforeEach(function () {
        var loginPage = new LoginPage();
        loginPage.visitPage();
        loginPage.login();
        dashboardPage = new DashboardPage();
    });

    afterEach(function () {
        dashboardPage.logout();
    });

    it('should have the correct title', function () {
        expect(dashboardPage.title.getText()).toBe('This is the dashboard view');
    });
});