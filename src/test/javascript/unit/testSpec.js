describe('TEST PAGE', function () {
    'use strict';

    beforeEach(module('app.projects'));

    it('TEST IT SHOULD BE TRUE', inject(function () {
        expect(true).toBeTruthy();
    }));
});