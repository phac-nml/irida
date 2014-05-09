module.exports = function (config) {
    var app_path = 'src/main/webapp',
        test_path = 'src/test/javascript';
    config.set({
        files: [
                app_path + '/static/scripts/bundle.js',
                app_path + '/bower_components/angular-mocks/angular-mocks.js',
                test_path + '/unit/**/*spec.js'
        ],
        basePath: '',
        frameworks: ['jasmine'],
        reporters: ['progress'],
        // web server port
        port: 9876,
        browsers: ['PhantomJS'],
        // If browser does not capture in given timeout [ms], kill it
        captureTimeout: 60000,
        autoWatch: false,
        singleRun: true,
        colors: true
    });
};