var LandingPage = (function () {
    'use strict';

    var username = 'test',
        password = 'password1';

    function LandingPage () {
        this.title = element(by.id('title'));
        this.username_tf = element(by.model('username'));
        this.password_tf = element(by.model('password'));
        this.button = element(by.css('.btn'));
    }

    LandingPage.prototype.visitPage = function() {
        browser.get('/');
    };

    LandingPage.prototype.login = function () {
        this.username_tf.sendKeys(username);
        this.password_tf.sendKeys(password);
        this.button.click();
    };

    return LandingPage;
})();

module.exports = LandingPage;
