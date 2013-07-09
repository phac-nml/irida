module.exports = function (grunt) {
    'use strict';

    // Load all grunt tasks
    require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);

    // Configure paths
    var iridaConfig = {
        dev: 'src/main/webapp/resources',
        test: 'src/test'
    };

    grunt.initConfig({
        irida: iridaConfig,

        jshint: {
            options: {
                jshintrc: '.jshintrc'
            },
            all: [
                'Gruntfile.js',
                '<%= irida.dev %>/js/{,*/}*.js',
                '!<%= irida.dev %>/js/lib/{,*/}*.js',
            ]
        },
        karma: {
            unit: {
                configFile: 'karma.conf.js',
                singleRun: true
            }
        },
        compass: {
            config: 'src/main/webapp/'
        }
    });

    grunt.registerTask('test', ['karma:unit']);

    grunt.registerTask('default', ['jshint']);
};