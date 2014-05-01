var proxySnippet = require('grunt-connect-proxy/lib/utils').proxyRequest;

module.exports = function (grunt) {
    'use strict';

    require('load-grunt-tasks')(grunt);
    require('time-grunt')(grunt);

    grunt.initConfig({
        path: {
            // configurable paths
            app: require('./bower.json').appPath,
            dist: 'src/main/webapp/dist'
        },
        // Automatically inject Bower components into the app
        bowerInstall: {
            app: {
                src: ['<%= path.app %>/pages/index.html'],
                ignorePath: '<%= path.app %>/'
            },
            sass: {
                src: ['<%= path.app %>/scss/{,*/}*.{scss,sass}'],
                ignorePath: '<%= path.app %>/bower_components/'
            }
        },
        // Empties folders to start fresh
        clean: {
            dist: {
                files: [{
                    dot: true,
                    src: [
                        '<%= path.app %>/static/*',
                        '!<%= path.app %>/.git*'
                    ]
                }]
            }
        },
        compass: {
            options: {
                sassDir: '<%= path.app %>/scss',
                cssDir: '<%= path.app %>/static/css',
                relativeAssets: false,
                assetCacheBuster: false,
                raw: 'Sass::Script::Number.precision = 10\n'
            },
            dev: {
                options: {
                    debugInfo: true
                }
            }
        },

        // Run some tasks in parallel to speed up the build process
        concurrent: {
            dev: [
                'compass:dev'
            ]
        },
        // The actual grunt server settings
        connect: {
            proxies: [
                {
                    context: '/',
                    host: 'localhost',
                    port: 8080,
                    https: false,
                    changeOrigin: false
                }
            ],
            options: {
                port: 9000,
                // Change this to '0.0.0.0' to access the server from outside.
                hostname: 'localhost',
                livereload: 35729
            },
            livereload: {
                options: {
                    open: true,
                    base: [
                        '<%= path.app %>'
                    ],
                    middleware: function (connect) {
                        return [
                            proxySnippet,
                            connect.static(require('path').resolve('<%= path.app %>'))
                        ];
                    }
                }
            }
        },
        jshint: {
            options: {
                jshintrc: '.jshintrc',
                reporter: require('jshint-stylish')
            },
            all: ['Gruntfile.js']
        },
        watch: {
            bower: {
                files: ['bower.json'],
                tasks: ['bowerInstall']
            },
            compass: {
                files: ['<%= path.app %>/scss/{,*/}*.scss'],
                tasks: ['compass:dev']
            },
            livereload: {
                options: {
                    livereload: '<%= connect.options.livereload %>'
                },
                files: [
                    '<%= path.app %>/pages/index.html',
                    '<%= path.app %>/static/css/{,*/}*.css',
                ]
            }
        }
    });

    grunt.registerTask('dev', [
        'clean:dist',
        'bowerInstall',
        'concurrent:dev',
        'configureProxies',
        'connect:livereload',
        'watch'
    ]);
};