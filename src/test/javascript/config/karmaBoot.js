var tests = [];
for (var file in window.__karma__.files)
{
    if (window.__karma__.files.hasOwnProperty(file))
    {
        if (/\.spec\.js$/.test(file))
        {
            tests.push(file);
        }
    }
}

requirejs.config
({
    // Karma serves files from '/base'
    baseUrl: '/base/src/main/webapp/resources/js/',

    paths: {
        'app' : '/base/src/main/webapp/resources/js/app',
        'routes': '/base/src/main/webapp/resources/js/routes',
        'services/dependencyResolverFor': '/base/src/main/webapp/resources/js/services/dependencyResolverFor'
    },

    // ask Require.js to load these files (all our tests)
    deps: tests,

    // start test run, once Require.js is done
    callback: window.__karma__.start
});