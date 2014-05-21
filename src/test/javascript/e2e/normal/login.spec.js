var LoginPage = require('./pages/login.page');
var DashboardPage = require('./pages/dashboard.page');

describe('Index', function () {
    'use strict';

    var loginPage = new LoginPage();

    it('Should say welcome', function () {
        loginPage.visitPage();

        expect(loginPage.title.getText()).toEqual('Welcome to the IRIDA Platform!');
    });

    it('Should be able to login', function () {
        loginPage.login();
        var dashboardPage = new DashboardPage();
        expect(dashboardPage.title.getText()).toBe('This is the dashboard view');
    });
});