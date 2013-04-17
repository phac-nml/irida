'use strict';

module.exports = function (grunt) {
  // Load all grunt tasks
  require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);

  // Configure paths
  var iridaConfig = {
    dev: 'src/main/webapp/resources/dev'
  };

  grunt.initConfig({
    irida: iridaConfig,

    connect: {
      test: {
        options: {
          port: 9000
        }
      }
    },
    jshint: {
      options: {
        jshintrc: '.jshintrc'
      },
      all: [
        'Gruntfile.js',
        'src/main/webapp/resources/dev/js/{,*/}*.js',
        '!src/main/webapp/resources/dev/js/vendor/*',
        'test/js/spec/{,*/}*.js'
      ]
    },
    karma: {
      unit: {
        configFile: 'karma.conf.js',
        singleRun: true
      }
    }
  });

  grunt.renameTask('regarde', 'watch');

  grunt.registerTask('test', [
    'connect:test',
    'karma:unit'
  ]);

  grunt.registerTask('default', [
    'jshint'
  ]);
};