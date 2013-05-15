basePath = '';

frameworks = ["ng-scenario"];

// list of files / patterns to load in the browser
files = [
  'src/test/js/e2e/**/*.js'
];

urlRoot = '/__karma/';

proxies = {
  '/': 'http://localhost:8080/'
};

// list of files to exclude
exclude = [];

browsers = ['Chrome', 'Firefox', 'PhantomJS'];

// test results reporter to use
// possible values: dots || progress || growl
reporters = ['progress'];

autoWatch = true;

// web server port
port = 9000;

// cli runner port
runnerPort = 9100;

plugins = [
  'karma-ng-scenario',
  'karma-phantomjs-launcher',
  'karma-firefox-launcher',
  'karma-chrome-launcher',
  'karma-junit-reporter'
];