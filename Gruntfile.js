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

    var webapp_dir = require('./bower.json').appPath;

    grunt.initConfig({
        // Create variables for the path used in this project.
        path: {
            app: webapp_dir,
            static: webapp_dir + '/static',
            bower: webapp_dir + '/bower_components',
            test: './src/test/javascript'
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
        // Browserify
        browserify: {
            dev: {
                files: {
                    '<%= path.static %>/scripts/bundle.js': ['<%= path.app %>/scripts/app.js']
                }
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
            },
            tmp: {
                files: [
                    {
                        dot: true,
                        src: [
                            '.tmp'
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
                javascriptsDir: '<%= path.app %>/scripts',
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
                'compass:dev',
                'browserify:dev'
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
            },
            coverage: {
                options: {
                    base: 'coverage/',
                    port: 5555,
                    keepalive: true
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
                            '*.html'
                        ]
                    },
                    {
                        expand: true,
                        dot: true,
                        cwd: '<%= path.app %>/views/',
                        dest: '<%= path.static %>/views',
                        src: [
                            '*.html'
                        ]
                    }
                ]
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
            all: [
                'Gruntfile.js',
                '<%= path.app %>/scripts/{,*/}*.js'
            ],
            test: {
                options: {
                    jshintrc: '<%= path.test %>/.jshintrc'
                },
                src: ['<%= path.test %>/{,*/}*.js']
            }
        },
        // Karma Testing
        karma: {
            allBrowsers: {
                configFile: 'karma-unit.conf.js',
                autoWatch: false,
                singleRun: true
            },
            dev: {
                configFile: 'karma-phantom.conf.js',
                autoWatch: false,
                singleRun: true
            },
            auto: {
                configFile: 'karma-unit.conf.js'
            },
            unit_coverage: {
                configFile: 'karma-unit.conf.js',
                autoWatch: false,
                singleRun: true,
                reporters: ['progress', 'coverage'],
                preprocessors: {
                    '<%= path.app %>/scripts/*.js': ['coverage']
                },
                coverageReporter: {
                    type: 'html',
                    dir: 'coverage/'
                }
            }
        },
        // ngmin tries to make the code safe for minification automatically by
        // using the Angular long form for dependency injection. It doesn't work on
        // things like resolve or inject so those have to be done manually.
        ngmin: {
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: '.tmp/concat/scripts',
                        src: '*.js',
                        dest: '.tmp/concat/scripts'
                    }
                ]
            }
        },
        protractor: {
            options: {
                configFile: 'protractor.conf.js', // Default config file
                keepAlive: true, // If false, the grunt process stops when the test fails.
                noColor: false // If true, protractor will not use colors in its output.
            },
            singleRun: {
            },
            auto: {
                options: {
                    keepAlive: true,
                    singleRun: false
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
                        // e.g. Replace src="/app.js" th:src="@{/12k23j3223k4.app.js}"
                        {
                            match: /src="scripts\/([a-zA-Z0-9]+).(\w+)\.js"/g,
                            replacement: 'th:src="@{/scripts/$1.$2.js}"'
                        },
                        // e.g. Repalces href="main.css" ==> th:href="@{/3k24jk234jk32.main.css}"
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
                            js: [],
                            css: ['cssmin']
                        },
                        post: {
                        }
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
                files: ['<%= path.app %>/styles/**/*.scss'],
                tasks: ['compass:dev', 'autoprefixer']
            },
            copy: {
                files: ['<%= path.app %>/pages/*'],
                tasks: ['copy:dev']
            },
            browserify: {
                files: ['<%= path.app %>/scripts/**/*.js'],
                tasks: ['browserify:dev', 'karma:dev'],
                options: {
                    livereload: true
                }
            },
            js: {
                files: ['<%= path.app %>/static/scripts/*.js', '<%= path.test %>/unit/**/*.js'],
                tasks: ['newer:jshint:all'],
                options: {
                    livereload: true
                }
            },
            jsTest: {
                files: ['<%= path.test %>/unit/**/*.js'],
                tasks: ['karma:dev']
            },
            livereload: {
                options: {
                    livereload: '<%= connect.options.livereload %>'
                },
                files: [
                    '<%= path.app %>/pages/index.html',
                    '<%= path.static %>/styles/{,*/}*.css'
                ]
            }
        }
    })
    ;

// Single run tests
    grunt.registerTask('test', ['jshint', 'karma:dev', 'test:e2e']);
    grunt.registerTask('test:e2e', [
        'clean:dist',
        'concurrent:dev',
        'autoprefixer',
        'browserify:dev',
        'protractor_webdriver',
        'protractor:singleRun'
    ]);
    grunt.registerTask('test:unit', ['browserify:dev', 'karma:unit']);

//coverage testing
    grunt.registerTask('test:coverage', ['browserify:dev', 'karma:unit_coverage']);


// Development task.
    grunt.registerTask('dev', [
        'clean:dist',
        'concurrent:dev',
        'autoprefixer:dev',
        'configureProxies',
        'connect:livereload',
        'protractor_webdriver',
        'watch'
    ]);

    grunt.registerTask('build', [
        'clean:dist',
        'useminPrepare',
        'concurrent:dist',
        'autoprefixer:dist',
        'ngmin',
        'copy:dist',
        'browserify:dev',
        'cssmin',
        'rev',
        'usemin',
        'replace',
        'clean:tmp'
    ]);

    grunt.registerTask('default', ['build']);
}
;