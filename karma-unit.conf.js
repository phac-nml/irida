module.exports = function (config) {
    var app_path = 'src/main/webapp',
        test_path = 'src/test/javascript/';
    config.set({
        basePath: '',
        frameworks: ['jasmine', 'requirejs'],
        files: [
                app_path + '/bower_components/requirejs/require.js',
            {
                pattern: app_path + '/bower_components/angular/angular.js',
                included: false
            },
            {
                pattern: app_path + '/bower_components/angular-mocks/angular-mocks.js',
                included: false
            },
            {
                pattern: app_path + '/bower_components/angular-route/angular-route.js',
                included: false
            },
            {
                pattern: app_path + '/bower_components/angularAMD/angularAMD.js',
                included: false
            },
            {
                pattern: app_path + '/bower_components/angularAMD/ngload.js',
                included: false
            },
            {
                pattern: app_path + '/scripts/**/*.js',
                included: false
            },
//            {
//                pattern: test_path + '/unit/app_no_ngload.spec.js',
//                watched: true,
//                included: false
//            },
            {
                pattern: test_path + '/unit/lib/app_no_ngload.js',
                included: false
            },
            {
                pattern: test_path + '/unit/*.spec.js',
                included: false
            },
                test_path + '/test-main.js'
        ],
        exclude: [
//                app_path + '/scripts/main.js'
        ],
        reporters: ['progress'],
        browsers: ['Chrome'],
        logLevel: config.LOG_INFO,
        autoWatch: true,
        singleRun: false,
        colors: true
    });
};