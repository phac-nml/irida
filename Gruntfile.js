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
    watch: {
      sass: {
        dev: {
          files: {
            '<%= irida.app %>/scss/users.scss': '<%= irida.app %>/css/users.css'
          }
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
        'test/javascript/spec/{,*/}*.js'
      ]
    }
  });

  grunt.renameTask('regarde', 'watch');

  grunt.registerTask('default', [
    'jshint'
  ]);
};