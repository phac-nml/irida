module.exports = function ($location) {
    'use strict';
    var ctrl = this;

    ctrl.error = false;
    if ($location.absUrl().search('error') > -1) {
        ctrl.error = true;
    }
};