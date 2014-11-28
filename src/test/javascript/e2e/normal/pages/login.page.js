var LandingPage = (function () {
    'use strict';

    var username = 'test',
        password = 'password1';

    function LandingPage () {
        this.title = element(by.id('title'));
        this.email_tf = element(by.model('email'));
        this.password_tf = element(by.model('password'));
        this.button = element(by.css('.btn'));
    }

    LandingPage.prototype.visitPage = function() {
        browser.get('/login');
    };

    LandingPage.prototype.login = function () {
        this.email_tf.sendKeys(username);
        this.password_tf.sendKeys(password);
        this.button.click();
    };

    return LandingPage;
})();

module.exports = LandingPage;
