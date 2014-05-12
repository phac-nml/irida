'use strict';
require('../bower_components/angular/angular');

angular
    .module('irida', [
        require('./modules/dashboard').name
    ]);