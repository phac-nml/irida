require.config({
    baseUrl: '/scripts',
    paths: {
        'angular': '/bower_components/angular/angular',
        'angular-route': '/bower_components/angular-route/angular-route'
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
    function(app){
        angular.bootstrap(document, ['app']);
    }
);