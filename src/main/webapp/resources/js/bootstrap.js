(function () {
    'use strict';

    var baseUrl = '/',
        baseElm = document.getElementById('baseUrl');

    if(baseElm !== null) {
        baseUrl = baseElm.value;
    }

    require.config({
        baseUrl: baseUrl + 'resources/js',
        paths: {
            'angular': baseUrl + 'resources/bower_components/angular/angular',
            'angular-route': baseUrl + 'resources/bower_components/angular-route/angular-route'
        },
        shim: {
            'app': {
                deps: ['angular', 'angular-route']
            },
            'angular-route': {
                deps: ['angular']
            }
        }
    });

    require
    (
        ['app'],
        function () {
            angular.bootstrap(document, ['app']);
        }
    );
})();