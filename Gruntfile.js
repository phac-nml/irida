module.exports = function (grunt) {
  require('load-grunt-tasks')(grunt);

  var app_path = require('./bower.json').appPath;
  grunt.initConfig({
    paths  : {
      html: app_path + '/pages',
      scss: app_path + '/styles',
      css : app_path + '/resources/css',
      js  : app_path + '/resources/js'
    },
    compass: {
      dev: {
        options: {
          sassDir: '<%= paths.scss %>',
          cssDir : '<%= paths.css %>'
        }
      }
    },
    jshint : {
      options: {
        reporter  : require('jshint-stylish'),
        'jshintrc': true
      },
      target : ['<%= paths.js %>/**/*.js', '!<%= paths.js %>/**/*-min.js', '!<%= paths.js %>/vendor/**/*.js']
    },
    watch  : {
      compass: {
        files  : ['<%= paths.scss %>/**/*.scss'],
        tasks  : ['compass:dev'],
        options: {
          livereload: true
        }
      },
      html   : {
        files  : ['<%= paths.html %>/**/*.html'],
        options: {
          livereload: true
        }
      },
      jshint : {
        files  : ['<%= paths.js %>/**/*.js'],
        tasks  : ['newer:jshint'],
        options: {
          livereload: true
        }
      }
    }
  });

  grunt.registerTask("default", []);
  grunt.registerTask("dev", ['compass:dev']);
};