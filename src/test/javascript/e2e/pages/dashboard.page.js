var DashboardPage = (function() {
    'use strict';

    function DashboardPage() {
        this.title = element(by.id('title'));
        this.logout_btn = element(by.id('logout_btn'));
    }

    DashboardPage.prototype.logout = function () {
        this.logout_btn.click();
    }

    return DashboardPage;
})();

module.exports = DashboardPage;