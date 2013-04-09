// Karma configuration
// Generated on Mon Apr 08 2013 22:16:54 GMT-0500 (CDT)


// base path, that will be used to resolve files and exclude
basePath = '';


// frameworks to use
frameworks = ['jasmine'];


// list of files / patterns to load in the browser
files = [
  'src/main/webapp/resources/dev/js/vendor/angular/angular.js',
  'src/main/webapp/resources/dev/js/vendor/angular/angular-sanitize.min.js',
  'src/main/webapp/resources/dev/js/vendor/angular/angular-mocks.js',
  'src/main/webapp/resources/dev/js/controllers/*.js',
  'src/test/javascript/spec/controllers/*.js'
];


// list of files to exclude
exclude = [
  
];


// test results reporter to use
// possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
reporters = ['progress'];


// web server port
port = 9876;


// cli runner port
runnerPort = 9100;


// enable / disable colors in the output (reporters and logs)
colors = true;


// level of logging
// possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
logLevel = LOG_INFO;


// enable / disable watching file and executing tests whenever any file changes
autoWatch = true;


// Start these browsers, currently available:
// - Chrome
// - ChromeCanary
// - Firefox
// - Opera
// - Safari (only Mac)
// - PhantomJS
// - IE (only Windows)
browsers = [];


// If browser does not capture in given timeout [ms], kill it
captureTimeout = 60000;


// Continuous Integration mode
// if true, it capture browsers, run tests and exit
singleRun = false;


// plugins to load
plugins = [
  'karma-jasmine'
];
