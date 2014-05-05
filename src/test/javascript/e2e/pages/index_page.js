var IndexPage = (function () {
    'use strict';

    function IndexPage () {
        this.title = element(by.id('title'));
    }

    IndexPage.prototype.visitPage = function() {
        browser.get('/');
    };

    return IndexPage;
})();

module.exports = IndexPage;
