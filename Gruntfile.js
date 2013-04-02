'use strict';

module.exports = function (grunt) {
  require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);

  grunt.initConfig({
    jshint: {
      options: {
        jshintrc: '.jshintrc'
      },
      all: [
        'Gruntfile.js',
        'src/main/webapp/resources/js/{,*/}*.js',
        '!src/main/webapp/resources/js/vendor/*',
        'test/javascript/spec/{,*/}*.js'
      ]
    }
  });

  grunt.registerTask('default', [
    'jshint'
  ]);
};