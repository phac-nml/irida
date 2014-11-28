exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
    seleniumPort: null,
    seleniumArgs: ['-browserTimeout=60'],

    chromeOnly: false,

    capabilities: {
        'browserName': 'phantomjs',
        'phantomjs.binary.path': './node_modules/phantomjs/bin/phantomjs'

    },

    specs: ['../e2e/**/*.spec.js'],

    baseUrl: 'http://localhost:8080',

    jasmineNodeOpts: {
        onComplete: null,
        isVerbose: false,
        showColors: true,
        includeStackTrace: false
    }
};