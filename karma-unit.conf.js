module.exports = function(config) {
    var app_path = 'src/main/webapp',
        test_path = 'src/test/javascript';
    config.set({
        files : [
            app_path + '/bower_components/angular/angular.js',
            app_path + '/bower_components/angular-mocks/angular-mocks.js',
            app_path + '/scripts/app.js',
            app_path + '/scripts/projectsPages.js',
            test_path + '/unit/**/*.js'
        ],
        basePath: '',
        frameworks: ['jasmine'],
        reporters: ['progress'],
        browsers: ['Chrome', 'Firefox'],
        autoWatch: false,
        singleRun: true,
        colors: true
    });
};