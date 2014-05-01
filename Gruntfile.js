/*
 * Grunt configuration file.
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */

var proxySnippet = require('grunt-connect-proxy/lib/utils').proxyRequest;

module.exports = function (grunt) {
    'use strict';

    require('load-grunt-tasks')(grunt);
    require('time-grunt')(grunt);

    grunt.initConfig({
        // Create variables for the path used in this project.
        path: {
            app: require('./bower.json').appPath,
            static: require('./bower.json').appPath + '/static'
        },
        // Add vendor prefixed styles
        autoprefixer: {
            options: {
                browsers: ['last 2 version', 'ie 8', 'ie 9']
            },
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: '.tmp/styles/',
                        src: '{,*/}*.css',
                        dest: '<%= path.static %>/styles/'
                    }
                ]
            }
        },
        // Automatically inject Bower components into the app
        bowerInstall: {
            app: {
                src: ['<%= path.app %>/pages/index.html'],
                ignorePath: '<%= path.app %>/'
            },
            sass: {
                src: ['<%= path.app %>/styles/{,*/}*.{scss,sass}'],
                ignorePath: '<%= path.app %>/bower_components/'
            }
        },
        // Empties folders to start fresh for both dev and production.
        clean: {
            dist: {
                files: [
                    {
                        dot: true,
                        src: [
                            '.tmp',
                            '<%= path.static %>/*',
                            '!<%= path.app %>/.git*'
                        ]
                    }
                ]
            }
        },
        // Used to compile the scss
        compass: {
            options: {
                sassDir: '<%= path.app %>/styles',
                cssDir: '.tmp/styles',
                importPath: '<%= path.app %>/bower_components',
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
        // The actual grunt server settings allows for live reload in the browser
        // and proxying 9000 --> 8080
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
                        '.tmp',
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
        // Need to make sure the JavaScript files are consistent.
        jshint: {
            options: {
                jshintrc: '.jshintrc',
                reporter: require('jshint-stylish')
            },
            all: ['Gruntfile.js']
        },
        // Watch for changes and do the right thing!
        watch: {
            bower: {
                files: ['bower.json'],
                tasks: ['bowerInstall']
            },
            compass: {
                files: ['<%= path.app %>/styles/{,*/}*.scss'],
                tasks: ['compass:dev', 'autoprefixer']
            },
            livereload: {
                options: {
                    livereload: '<%= connect.options.livereload %>'
                },
                files: [
                    '<%= path.app %>/pages/index.html',
                    '<%= path.static %>/styles/{,*/}*.css',
                ]
            }
        }
    });

    // Development task.
    grunt.registerTask('dev', [
        'clean:dist',
        'bowerInstall',
//        'concurrent:dev',
        'compass:dev',
        'autoprefixer',
        'configureProxies',
        'connect:livereload',
        'watch'
    ]);

    grunt.registerTask('build', [
        'clean:dist'
    ]);

    grunt.registerTask('test', []);

    grunt.registerTask('default', [
        'test',
        'build'
    ]);
};