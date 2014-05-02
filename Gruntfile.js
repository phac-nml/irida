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
            dev: {
                files: [
                    {
                        expand: true,
                        cwd: '.tmp/styles/',
                        src: '{,*/}*.css',
                        dest: '<%= path.static %>/styles/'
                    }
                ]
            },
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: '.tmp/styles/',
                        src: '{,*/}*.css',
                        dest: '.tmp/styles/'
                    }
                ]
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
            dist: {
                options: {
                    environment: 'production'
                }
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
                'copy:dev',
                'compass:dev'
            ],
            dist: [
                'compass:dist'
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
        // Copy index during dev
        copy: {
            dev: {
                expand: true,
                dot: true,
                cwd: '<%= path.app %>/pages',
                dest: '<%= path.static %>/',
                src: ['*.html']
            },
            dist: {
                files: [
                    {
                        expand: true,
                        dot: true,
                        cwd: '<%= path.app %>/pages',
                        dest: '<%= path.static %>/',
                        src: [
                            '*.html',
                        ]
                    },
                    {
                        expand: true,
                        dot: true,
                        cwd: '<%= path.app %>/views/',
                        dest: '<%= path.static %>/views',
                        src: [
                            '*.html',
                        ]
                    }
                ]
            }
        },
        // Minify CSS
        cssmin: {
            options: {
                root: '<%= path.app %>'
            }
        },
        htmlmin: {
            dist: {
                options: {
                    collapseWhitespace: true,
                    collapseBooleanAttributes: true,
                    removeCommentsFromCDATA: true,
                    removeOptionalTags: true
                },
                files: [
                    {
                        expand: true,
                        cwd: '<%= path.static %>',
                        src: ['*.html', 'views/{,*/}*.html'],
                        dest: '<%= path.static %>'
                    }
                ]
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
        // ngmin tries to make the code safe for minification automatically by
        // using the Angular long form for dependency injection. It doesn't work on
        // things like resolve or inject so those have to be done manually.
        ngmin: {
            dist: {
                expand: true,
                dot: true,
                cwd: '<%= path.app %>/scripts',
                src: ['*.js'],
                dest: '.tmp/scripts/'
            }
        },
        protractor: {
            options: {
                configFile: "node_modules/protractor/referenceConf.js", // Default config file
                keepAlive: true, // If false, the grunt process stops when the test fails.
                noColor: false, // If true, protractor will not use colors in its output.
                args: {
                    // Arguments passed to the command
                }
            },
            dev: {
                options: {
                    configFile: "protractor.conf.js", // Target-specific config file
                    args: {} // Target-specific arguments
                }
            }
        },
        protractor_webdriver: {
            dev: {
            }
        },
        // Replace
        // This is needed to add thymeleaf to the templates that are autogenerated.
        replace: {
            dist: {
                options: {
                    patterns: [
                        {
                            match: /src="scripts\/(\w+)\.js"/g,
                            replacement: 'th:src="@{/scripts/$1.js}"'
                        },
                        {
                            match: /href="styles\/([a-zA-Z0-9]+).(\w+).css"/g,
                            replacement: 'th:href="@{/styles/$1.$2.css}"'
                        }
                    ]
                },
                files: [
                    {
                        src: ['<%= path.static %>/index.html'],
                        dest: '<%= path.static %>/index.html'
                    }
                ]
            }
        },
        // Renames files for browser caching purposes
        rev: {
            dist: {
                files: {
                    src: [
                        '<%= path.static %>/scripts/{,*/}*.js',
                        '<%= path.static %>/styles/{,*/}*.css',
                        '<%= path.static %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}',
                        '<%= path.static %>/styles/fonts/*'
                    ]
                }
            }
        },
        // Reads HTML for usemin blocks to enable smart builds that automatically
        // concat, minify and revision files. Creates configurations in memory so
        // additional tasks can operate on them
        useminPrepare: {
            html: '<%= path.app %>/pages/index.html',
            options: {
                dest: '<%= path.static %>',
                flow: {
                    html: {
                        steps: {
                            js: ['concat', 'uglifyjs'],
                            css: ['cssmin']
                        },
                        post: {}
                    }
                }
            }
        },

        // Performs rewrites based on rev and the useminPrepare configuration
        usemin: {
            html: ['<%= path.static %>/{,*/}*.html'],
            css: ['<%= path.static %>/styles/{,*/}*.css'],
            options: {
                assetsDirs: ['<%= path.static %>']
            }
        },
        // Watch for changes and do the right thing!
        watch: {
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
        'concurrent:dev',
        'autoprefixer:dev',
        'configureProxies',
        'connect:livereload',
        'watch'
    ]);

    grunt.registerTask('build', [
        'clean:dist',
        'useminPrepare',
        'concurrent:dist',
        'autoprefixer:dist',
        'ngmin',
        'copy:dist',
        'cssmin',
        'uglify',
        'rev',
        'usemin',
        'replace',
        'htmlmin'
    ]);

    grunt.registerTask('test-e2e', [
        'protractor_webdriver',
        'protractor'
    ]);

    grunt.registerTask('test', [
        'clean:dist',
        'concurrent:dev',
        'autoprefixer',
        'test-e2e'
    ]);
};