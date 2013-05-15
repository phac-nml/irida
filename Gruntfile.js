'use strict';

module.exports = function(grunt) {
  // Load all grunt tasks
  require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);

  // Configure paths
  var iridaConfig = {
    dev: 'src/main/webapp/resources/dev',
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
    }
  });

  grunt.registerTask('test', ['karma:unit']);

  grunt.registerTask('default', ['jshint']);
};